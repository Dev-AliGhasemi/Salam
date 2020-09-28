package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;

import ir.vira.salam.Adapters.ViewPagerAdapter;
import ir.vira.salam.Fragments.SplashSecurityFragment;

public class SplashActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeViews();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_name) , Context.MODE_PRIVATE);
        if (sharedPreferences.contains(getString(R.string.shared_key_enter)))
            startActivity(new Intent(this , MainActivity.class));
        else {
            viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 1)
                        ((SplashSecurityFragment) ((FragmentStatePagerAdapter) viewPager.getAdapter()).getItem(0)).preventDisplayingImageViewHelp();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.splashViewPager);
    }

}