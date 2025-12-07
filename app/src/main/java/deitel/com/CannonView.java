package deitel.com;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Random;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CannonView";

    public static final int MISS_PENALTY = 2;
    public static final int HIT_REWARD = 3;

    public static final double CANNON_BASE_RADIUS_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_WIDTH_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_LENGTH_PERCENT = 1.0 / 10;
    public static final double CANNONBALL_RADIUS_PERCENT = 3.0 / 80;
    public static final double CANNONBALL_SPEED_PERCENT = 3.0 / 2;
    public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
    public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
    public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
    public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
    public static final double TARGET_PIECES = 9;
    public static final double TARGET_MIN_SPEED_PERCENT = 3.0 / 4;
    public static final double TARGET_MAX_SPEED_PERCENT = 6.0 / 4;
    public static final double BLOCKER_WIDTH_PERCENT = 1.0 / 40;
    public static final double BLOCKER_LENGTH_PERCENT = 1.0 / 4;
    public static final double BLOCKER_X_PERCENT = 1.0 / 2;
    public static final double BLOCKER_SPEED_PERCENT = 1.0;
    public static final double TEXT_SIZE_PERCENT = 1.0 / 30;

    private CannonThread cannonThread;
    private FragmentActivity activity;
    private boolean dialogIsDisplayed = false;

    private Cannon cannon;
    private Blocker blocker;
    private ArrayList<Target> targets;

    private int screenWidth;
    private int screenHeight;

    private boolean gameOver;
    private double timeLeft;
    private int shotsFired;
    private double totalElapsedTime;

    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    private SoundPool soundPool;
    private SparseIntArray soundMap;

    private Paint textPaint;
    private Paint backgroundPaint;

    private CannonActivityCallback callback;

    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (FragmentActivity) context;
        getHolder().addCallback(this);

        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(attr)
                .build();

        soundMap = new SparseIntArray();
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));

        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }

    public void setActivityCallback(CannonActivityCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = w;
        screenHeight = h;
        textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));
        textPaint.setAntiAlias(true);
    }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }

    public void playSound(int soundId) {
        if (soundPool != null) soundPool.play(soundMap.get(soundId), 1, 1, 1, 0, 1f);
    }

    public void newGame() {
        if (cannonThread != null) cannonThread.setRunning(false);

        cannon = new Cannon(this,
                (int) (CANNON_BASE_RADIUS_PERCENT * screenHeight),
                (int) (CANNON_BARREL_LENGTH_PERCENT * screenWidth),
                (int) (CANNON_BARREL_WIDTH_PERCENT * screenHeight));

        Random random = new Random();
        targets = new ArrayList<>();
        int targetX = (int) (TARGET_FIRST_X_PERCENT * screenWidth);
        int targetY = (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) * screenHeight);

        for (int n = 0; n < TARGET_PIECES; n++) {
            double velocity = screenHeight * (random.nextDouble() *
                    (TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT) +
                    TARGET_MIN_SPEED_PERCENT);
            int color = (n % 2 == 0) ?
                    getResources().getColor(R.color.dark, getContext().getTheme()) :
                    getResources().getColor(R.color.light, getContext().getTheme());
            velocity *= -1;

            targets.add(new Target(this, color, HIT_REWARD, targetX, targetY,
                    (int) (TARGET_WIDTH_PERCENT * screenWidth),
                    (int) (TARGET_LENGTH_PERCENT * screenHeight),
                    (int) velocity));
            targetX += (TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT) * screenWidth;
        }

        blocker = new Blocker(this, Color.BLACK, MISS_PENALTY,
                (int) (BLOCKER_X_PERCENT * screenWidth),
                (int) ((0.5 - BLOCKER_LENGTH_PERCENT / 2) * screenHeight),
                (int) (BLOCKER_WIDTH_PERCENT * screenWidth),
                (int) (BLOCKER_LENGTH_PERCENT * screenHeight),
                (float) (BLOCKER_SPEED_PERCENT * screenHeight));

        timeLeft = 10;
        shotsFired = 0;
        totalElapsedTime = 0.0;
        gameOver = false;
        dialogIsDisplayed = false;

        if (cannonThread != null) cannonThread.setRunning(true);

        hideSystemBars();
    }

    private void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0;
        if (interval > 0.05) interval = 0.05;

        if (cannon.getCannonball() != null)
            cannon.getCannonball().update(interval);
        blocker.update(interval);

        for (GameElement target : targets)
            target.update(interval);

        timeLeft -= interval;

        if (timeLeft <= 0 && !gameOver) {
            timeLeft = 0;
            gameOver = true;
            if (cannonThread != null) cannonThread.setRunning(false);
            activity.runOnUiThread(() -> showGameOverDialog(R.string.lose));
        }

        if (targets.isEmpty() && !gameOver) {
            gameOver = true;
            if (cannonThread != null) cannonThread.setRunning(false);
            activity.runOnUiThread(() -> showGameOverDialog(R.string.win));
        }
    }

    public void alignAndFireCannonball(MotionEvent event) {
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());
        double centerMinusY = (screenHeight / 2 - touchPoint.y);
        double angle = Math.atan2(touchPoint.x, centerMinusY);
        cannon.align(angle);

        if (cannon.getCannonball() == null || !cannon.getCannonball().isOnScreen()) {
            cannon.fireCannonball();
            shotsFired++;
        }
    }

    private void showGameOverDialog(final int messageId) {
        if (dialogIsDisplayed) return;

        dialogIsDisplayed = true;

        activity.runOnUiThread(() -> {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                GameOverDialog dialog = GameOverDialog.newInstance(messageId, shotsFired, totalElapsedTime);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), "results");
            }
        });
    }

    public void drawGameElements(Canvas canvas) {
        if (canvas == null) return;
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft),
                50, 100, textPaint);
        cannon.draw(canvas);

        if (cannon.getCannonball() != null && cannon.getCannonball().isOnScreen())
            cannon.getCannonball().draw(canvas);

        blocker.draw(canvas);

        for (GameElement target : targets)
            target.draw(canvas);
    }

    public void testForCollisions() {
        if (cannon.getCannonball() != null && cannon.getCannonball().isOnScreen()) {
            for (int n = 0; n < targets.size(); n++) {
                if (cannon.getCannonball().collidesWith(targets.get(n))) {
                    targets.get(n).playSound();
                    timeLeft += targets.get(n).getHitReward();
                    cannon.removeCannonball();
                    targets.remove(n);
                    n--;
                    break;
                }
            }
        } else {
            cannon.removeCannonball();
        }

        if (cannon.getCannonball() != null && cannon.getCannonball().collidesWith(blocker)) {
            blocker.playSound();
            cannon.getCannonball().reverseVelocityX();
            timeLeft -= blocker.getMissPenalty();
        }
    }

    public void stopGame() {
        if (cannonThread != null)
            cannonThread.setRunning(false);
    }

    public void releaseResources() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (cannonThread == null) {
            cannonThread = new CannonThread(holder);
            cannonThread.setRunning(true);
            cannonThread.start();
            newGame();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (cannonThread != null) {
            cannonThread.setRunning(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)
            alignAndFireCannonball(e);
        return true;
    }

    private class CannonThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;

        public CannonThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("CannonThread");
        }

        public void setRunning(boolean running) {
            threadIsRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            long previousFrameTime = System.currentTimeMillis();

            while (true) {
                if (!threadIsRunning) {
                    try { Thread.sleep(16); } catch (InterruptedException e) {}
                    continue;
                }

                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        if (elapsedTimeMS > 50) elapsedTimeMS = 50;
                        totalElapsedTime += elapsedTimeMS / 1000.0;

                        updatePositions(elapsedTimeMS);
                        testForCollisions();
                        drawGameElements(canvas);

                        previousFrameTime = currentTime;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro na thread do jogo", e);
                } finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }

                try { Thread.sleep(16); } catch (InterruptedException e) {}
            }
        }
    }

    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static class GameOverDialog extends DialogFragment {

        private static final String ARG_MESSAGE_ID = "messageId";
        private static final String ARG_SHOTS_FIRED = "shotsFired";
        private static final String ARG_TOTAL_TIME = "totalElapsedTime";

        public GameOverDialog() {}

        public static GameOverDialog newInstance(int messageId, int shotsFired, double totalElapsedTime) {
            GameOverDialog dialog = new GameOverDialog();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE_ID, messageId);
            args.putInt(ARG_SHOTS_FIRED, shotsFired);
            args.putDouble(ARG_TOTAL_TIME, totalElapsedTime);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Bundle args = getArguments();
            int messageId = args.getInt(ARG_MESSAGE_ID);
            int shotsFired = args.getInt(ARG_SHOTS_FIRED);
            double totalElapsedTime = args.getDouble(ARG_TOTAL_TIME);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle(getResources().getString(messageId));
            builder.setMessage(getResources().getString(
                    R.string.results_format, shotsFired, totalElapsedTime));
            builder.setPositiveButton(R.string.reset_game, (dialog, which) -> {
                if (getActivity() instanceof CannonActivityCallback) {
                    ((CannonActivityCallback) getActivity()).resetGame();
                }
            });
            return builder.create();
        }
    }

    public interface CannonActivityCallback {
        void resetGame();
    }
}