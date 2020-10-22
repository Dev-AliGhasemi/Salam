package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;

import ir.vira.network.NetworkInformation;
import ir.vira.salam.Adapters.ViewPagerAdapter;
import ir.vira.salam.Fragments.SplashSecurityFragment;

public class SplashActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeViews();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_name), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(getString(R.string.shared_key_enter))) {
            try {
                checkNetwork();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
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

    public void btnConnectionClickListener(View view) {
        try {
            checkNetwork();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.splashViewPager);
        button = findViewById(R.id.splashButtonCheckConnection);
    }

    private void checkNetwork() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        button.setVisibility(View.INVISIBLE);
        NetworkInformation networkInformation = new NetworkInformation(this);
        if (networkInformation.isWifiEnabled()) {
            if (networkInformation.isConnectedToNetwork())
                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            else
                button.setVisibility(View.VISIBLE);
        } else if (networkInformation.isWifiAccessPointEnabled()) {
            startActivity(new Intent(this, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("isAdmin" , true));
        } else
            button.setVisibility(View.VISIBLE);
    }

}