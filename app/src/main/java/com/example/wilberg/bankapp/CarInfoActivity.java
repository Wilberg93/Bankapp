package com.example.wilberg.bankapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WILBERG on 2/12/2017.
 */
public class CarInfoActivity extends AppCompatActivity implements OnConnectionErrorListener {

    // Intent-related items
    private static final String EXTRA_START_PAGE_POSITION = "com.example.wilberg.bankapp.EXTRA_START_PAGE_POSITION";
    private static final String EXTRA_CURRENT_PAGE_POSITION = "com.example.wilberg.bankapp.EXTRA_CURRENT_PAGE_POSITION";

    // Bundle-related items
    private static final String BUNDLE_CURRENT_PAGE_POSITION = "BUNDLE_CURRENT_PAGE_POSITION";

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mActivityFinished) {
                ImageView sharedElement = mCurrentDetailsFragment.getCarImage();
                if (sharedElement == null) {
                    // If shared element is null, then it has been scrolled off screen and
                    // no longer visible. In this case we cancel the shared element transition by
                    // removing the shared element from the shared elements map.
                    names.clear();
                    sharedElements.clear();
                } else if (mStartPosition != mCurrentPosition) {
                    // If the user has swiped to a different ViewPager page, then we need to
                    // remove the old shared element and replace it with the new shared element
                    // that should be transitioned instead.
                    names.clear();
                    sharedElements.clear();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        names.add(sharedElement.getTransitionName());
                        sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                    }
                    else {
                        //TODO: pre LOLLIPOP
                    }
                }
            }
        }
    };

    private CarInfoFragment mCurrentDetailsFragment;
    private int mStartPosition;
    private int mCurrentPosition;
    private boolean mActivityFinished;

    private FloatingActionButton shareFAB;
    private Toolbar toolbarFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) postponeEnterTransition();
        setEnterSharedElementCallback(mCallback);

        mStartPosition = getIntent().getIntExtra(EXTRA_START_PAGE_POSITION, 0);
        if(savedInstanceState == null)
            mCurrentPosition = mStartPosition;
        else
            mCurrentPosition = savedInstanceState.getInt(BUNDLE_CURRENT_PAGE_POSITION);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        final DetailsFragmentPagerAdapter pagerAdapter = new DetailsFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(mCurrentPosition);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;


            }
        });

        shareFAB = (FloatingActionButton) findViewById(R.id.shareFAB);
        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareFAB.hide();
                showToolbarFAB();
            }
        });
        toolbarFAB = (Toolbar) findViewById(R.id.toolbarFAB);
        toolbarFAB.bringToFront();
        toolbarFAB.setOnClickListener(new View  .OnClickListener() {
            @Override
            public void onClick(View view) {
                hideToolbarFAB();
            }
        });
    }

    public void showToolbarFAB() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = (int) ((screenWidth) - (shareFAB.getWidth() + shareFAB.getWidth() + (getResources().getDimension(R.dimen.keyline_1))));
            int cy = (int) (getResources().getDimension(R.dimen.large_toolbar_size)) / 2;
            int finalRadius = Math.max(toolbarFAB.getWidth(), toolbarFAB.getHeight());
            Animator animator = ViewAnimationUtils.createCircularReveal(toolbarFAB, cx, cy, 0, finalRadius);
            toolbarFAB.setVisibility(View.VISIBLE);
            animator.start();
        }
        else {
            //TODO: pre LOLLIPOP
        }
    }

    public void hideToolbarFAB() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = toolbarFAB.getWidth() - ((shareFAB.getWidth() / 2) + (int) (getResources().getDimension(R.dimen.keyline_1) / getResources().getDisplayMetrics().density));
            int cy = toolbarFAB.getHeight() / 2;
            int initialRadius = Math.max(toolbarFAB.getWidth(), toolbarFAB.getHeight());
            Animator animator = ViewAnimationUtils.createCircularReveal(toolbarFAB, cx, cy, initialRadius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    toolbarFAB.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        }
        else {
            //TODO: pre LOLLIPOP
        }
        shareFAB.show();
    }

    @Override
    public void handleConnectionError() {
    }

    private class DetailsFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public DetailsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CarInfoFragment.newInstance(getSupportFragmentManager(), position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentDetailsFragment = (CarInfoFragment) object;

        }

        @Override
        public int getCount() {
            //TODO: return listSize
            return 10;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_CURRENT_PAGE_POSITION, mCurrentPosition);
    }

    /*
    @Override
    public void onBackPressed() {
        mCurrentDetailsFragment.hideFab();
        supportFinishAfterTransition();
    }
    */

    @Override
    public void finishAfterTransition() {
        mActivityFinished = true;
        Intent returningIntent = new Intent();
        returningIntent.putExtra(EXTRA_START_PAGE_POSITION, mStartPosition);
        returningIntent.putExtra(EXTRA_CURRENT_PAGE_POSITION, mCurrentPosition);
        setResult(RESULT_OK, returningIntent);
        super.finishAfterTransition();
    }
}