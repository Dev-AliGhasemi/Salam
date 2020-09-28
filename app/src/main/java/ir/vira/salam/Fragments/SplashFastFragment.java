package ir.vira.salam.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import ir.vira.salam.MainActivity;
import ir.vira.salam.R;

public class SplashFastFragment extends Fragment {

    private Button buttonEnter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_fast, container, false);
        initializeViews(view);
        buttonEnter.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(getResources().getString(R.string.shared_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.shared_key_enter), true);
            editor.commit();
        });
        return view;
    }

    private void initializeViews(View view) {
        buttonEnter = view.findViewById(R.id.splashButtonEnter);
    }
}
