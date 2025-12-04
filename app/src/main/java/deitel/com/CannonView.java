package deitel.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Random;



public class CannonView extends SurfaceView implements SurfaceHolder.Callback {

    //Topico 6.13
    private static final String TAG = "CannonView";
    private static final double MISS_PENALTY = 2; // Segundos subtraídos
    private static final double HIT_REWARD = 3; // Segundos adicionados
    private Cannon cannon; // O canhão
    private Blocker blocker; // O bloqueador
    private ArrayList<Target> targets; // Lista de alvos
    private int screenWidth; // Largura da tela
    private int screenHeight; // Altura da tela
    private CannonThread cannonThread; // A thread do game loop
    private SurfaceHolder surfaceHolder; // Gerencia o SurfaceView
    private boolean dialogIsDisplayed = false; // Se o AlertDialog está na tela
    private double totalElapsedTime; // Tempo total decorrido
    private double timeLeft; // Tempo restante no jogo
    private int shotsFired; // Número de tiros disparados
    private boolean gameOver; // Se o jogo acabou
    private SoundPool soundPool; // Gerencia os efeitos sonoros
    private SparseIntArray soundMap; // Mapeia IDs de som para recursos
    private final int TARGET_SOUND_ID = 0;
    private final int CANNON_SOUND_ID = 1;
    private final int BLOCKER_SOUND_ID = 2;
    private Paint textPaint; // Para desenhar texto (tempo, tiros)
    private Paint backgroundPaint; // Para desenhar o fundo
    private Activity activity; // Referência à Activity para mostrar o Dialog

    // Construtor
    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity) context;

        // Inicializa a lista de alvos
        targets = new ArrayList<>();

        // Obtém o SurfaceHolder e adicionar o callback
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Configurar o SoundPool
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(1);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();

        // Configurar o mapa de sons
        soundMap = new SparseIntArray(3);
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));

        // Configurar os objetos Paint
        textPaint = new Paint();
        textPaint.setTextSize(screenWidth / 20); // Tamanho será ajustado em onSizeChanged
        textPaint.setColor(Color.WHITE);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }



    //Topico 6.13.8
    public void alignAndFireCannonball(MotionEvent event){

        Point touchPoint = new Point((int) event.getX(),
        (int) event.getY());

        double centerMinusY = (screenHeight / 2 - touchPoint.y);

        double angle = 0;

        angle = Math.atan2(touchPoint.x, centerMinusY);

        cannon.align(angle);

        if (cannon.getCannonball() == null ||
            !cannon.getCannonball().isOnScreen()){
            cannon.fireCannonball();
            ++shotsFired;
        }
    }

    //topico 6.13.9
    private void showGameOverDialog(final int messageId){
        final DialogFragment gameResult = new DialogFragment(){

            @Override
            public Dialog onCreateDialog(Bundle bundle){

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(getResources().getString(messageId));

                builder.setMessage(getResources().getString(
                        R.string.results_format, shotsFired, totalElapsedTime));
                builder.setPositiveButton(R.string.reset_game,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogIsDisplayed = false;
                                newGame();
                            }
                        }
                );
                return builder.create();
            }
        };

        activity.runOnUiThread(
                new Runnable() {
                    public void run(){
                        showSystemBars();
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false);
                        gameResult.show(activity.getFragmentManager(), "results");
                    }
                }
        );
    }



    //Topico 6.13.14
    @Override
    public boolean onTouchEvent(MotionEvent e){

        int action = e.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
            alignAndFireCannonball(e);
        }
        return true;
    }

}
