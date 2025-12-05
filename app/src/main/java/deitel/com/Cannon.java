package deitel.com;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {
    //Topico 6.11.1
    private int baseRadius;
    private int barrelLenght;
    private Point barrelEnd = new Point();
    private double barrelAngle;
    private Cannonball cannonball;
    private Paint paint = new Paint();
    private CannonView view;

    //constructor
    public Cannon(CannonView view, int baseRadius, int barrelLenght, int barrelWidth){
        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLenght = barrelLenght;
        paint.setStrokeWidth(barrelWidth);
        paint.setColor(Color.BLACK);
        align(Math.PI / 2);
    }

    //Topico 6.11.2
    public void align(double barrelAngle){
        this.barrelAngle = barrelAngle;
        barrelEnd.x = (int) (barrelLenght * Math.sin(barrelAngle));
        barrelEnd.y = (int) (-barrelLenght * Math.cos(barrelAngle)) +
                view.getScreenHeight() / 2;
    }

    //Topico 6.11.3
    public void fireCannonball(){

        int velocityX = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
                view.getScreenWidth() * Math.sin(barrelAngle));

        int velocityY = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
                view.getScreenWidth() * -Math.cos(barrelAngle));

        int radius = (int) (view.getScreenHeight() *
                CannonView.CANNONBALL_RADIUS_PERCENT);

        cannonball = new Cannonball (view, Color.BLACK,
                CannonView.CANNON_SOUND_ID, -radius,
                view.getScreenHeight() / 2 - radius, radius, velocityX,
                velocityY);

        cannonball.playSound();
    }

    //Topico 6.11.4
    public void draw(Canvas canvas){

        canvas.drawLine(0, view.getScreenHeight() / 2, barrelEnd.x,
                barrelEnd.y, paint);

        canvas.drawCircle(0, (int) view.getScreenHeight() / 2,
                (int) baseRadius, paint);

    }

    //Topico 6.11.5
    public Cannonball getCannonball(){
        return cannonball;
    }

    public void removeCannonball(){
        cannonball = null;
    }







}
