package com.cdh.otsimplex.detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cdh.otsimplex.MainActivity;
import com.cdh.otsimplex.R;
import com.cdh.otsimplex.detail.animation.DepthPageTransformer;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private ArrayList<Bundle> bundles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int size = getIntent().getIntExtra(MainActivity.SIZE, 0);
        bundles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            bundles.add(getIntent().getBundleExtra(Integer.toString(i)));
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), size);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        HashMap<Integer, Fragment> fragments;
        private int size;

        /**
         * Construtor
         */
        public ScreenSlidePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            fragments = new HashMap<>();
            this.size = size;
        }

        /**
         * Criacao de view e passando parametros necessarios.
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = DetailPageFragment.newInstance();

                fragment.setArguments(bundles.get(position));
                fragments.put(position, fragment);
            }
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return size;
        }
    }
}
