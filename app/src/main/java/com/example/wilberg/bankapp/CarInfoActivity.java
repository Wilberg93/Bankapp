package com.example.wilberg.bankapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by WILBERG on 2/12/2017.
 */
public class CarInfoActivity extends AppCompatActivity {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String LIST_POSITION = "com.example.wilberg.bankapp.LIST_POSITION";

    private CarInfoFragment mCurrentDetailsFragment;
    private int mStartPosition;
    private int mCurrentPosition;

    public static CarInfoActivity newInstance(String carID) {
        Bundle args = new Bundle();
        args.putString(CAR_ID, carID);
        CarInfoActivity infoFragment = new CarInfoActivity();
        return infoFragment;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        postponeEnterTransition();

        mStartPosition = getIntent().getIntExtra(LIST_POSITION, 0);
        mCurrentPosition = mStartPosition;

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new DetailsFragmentPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(mCurrentPosition);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }
        });


        if(1 == 2) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            //CarInfoFragment carInfo = CarInfoFragment.newInstance(getIntent().getStringExtra(CAR_ID));

                getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));

            // getSupportFragmentManager().beginTransaction().replace(android.R.id.content, carInfo).commit();
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        //resultIntent.putExtra(IS_CHECKED_BOOLEAN, isFavoritedCheckBox.isChecked());
        //resultIntent.putExtra(CAR_ID, selectedCar.getCarID());
        setResult(1, resultIntent);
        supportFinishAfterTransition();
    }

    private class DetailsFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public DetailsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CarInfoFragment.newInstance(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentDetailsFragment = (CarInfoFragment) object;
        }

        @Override
        public int getCount() {
            return 10;
        }
    }
}
