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
