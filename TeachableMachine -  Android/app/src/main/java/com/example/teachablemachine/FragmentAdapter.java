package com.example.teachablemachine;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Login", "Sign Up"};
    private Context context;

    public FragmentAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new login();
        }
        else{

            return new signup();
        }

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}


