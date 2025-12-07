package deitel.com;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements CannonView.CannonActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void resetGame() {
        // Obtemos o fragmento do container
        MainActivityFragment fragment = (MainActivityFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (fragment != null) {
            fragment.resetCannonGame();
        }
    }
}