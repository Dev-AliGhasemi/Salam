package ir.vira.salam.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ir.vira.salam.R;

public class SplashSecurityFragment extends Fragment {

    private ImageView imageViewTouchHelp;
    private boolean isImageViewDisplay = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_security, container, false);
        initializeViews(view);
        if (isImageViewDisplay) {
            imageViewTouchHelp.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageViewTouchHelp.setVisibility(View.VISIBLE);
                    float fromX = imageViewTouchHelp.getWidth();
                    float toX = imageViewTouchHelp.getWidth() * -1;
                    TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, 0, 0);
                    translateAnimation.setRepeatCount(Animation.INFINITE);
                    translateAnimation.setRepeatMode(Animation.REVERSE);
                    translateAnimation.setDuration(1000);
                    imageViewTouchHelp.startAnimation(translateAnimation);
                    imageViewTouchHelp.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        return view;
    }

    /**
     * This method change visibility of image view help when view pager scrolled
     */
    public void preventDisplayingImageViewHelp() {
        isImageViewDisplay = false;
        imageViewTouchHelp.setAnimation(null);
        imageViewTouchHelp.setVisibility(View.INVISIBLE);
    }

    private void initializeViews(View view) {
        imageViewTouchHelp = view.findViewById(R.id.splashImageTouchHelp);
    }
}
