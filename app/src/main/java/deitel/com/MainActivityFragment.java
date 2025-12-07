package deitel.com;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class MainActivityFragment extends Fragment {

    private CannonView cannonView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        cannonView = view.findViewById(R.id.cannonView);
        cannonView.setActivityCallback((CannonView.CannonActivityCallback) getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Permite que os botões de volume controlem o volume do jogo
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPause() {
        super.onPause();
        cannonView.stopGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cannonView.releaseResources();
    }

    public void resetCannonGame() {
        if (cannonView != null) {
            cannonView.stopGame();
            cannonView.newGame();
        }
    }
}