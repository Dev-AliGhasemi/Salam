package ir.vira.salam.Adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ir.vira.salam.Fragments.SplashFastFragment;
import ir.vira.salam.Fragments.SplashSecurityFragment;
import ir.vira.salam.Fragments.SplashStrongFragment;

/**
 * This adapter for set fragments in view pager for intro in splash activity
 *
 * @author Ali Ghasemi
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new SplashSecurityFragment());
        fragments.add(new SplashStrongFragment());
        fragments.add(new SplashFastFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getCount() {
        return fragments.size();
    }
}
