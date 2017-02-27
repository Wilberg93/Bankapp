package com.example.wilberg.bankapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.transition.Fade;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by WILBERG on 2/12/2017.
 */
public class CarInfoActivity extends AppCompatActivity {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";

    private static ImageView carImage;

    public static CarInfoActivity newInstance(String carID, ImageView carImage2) {
        Bundle args = new Bundle();
        args.putString(CAR_ID, carID);
        CarInfoActivity infoFragment = new CarInfoActivity();
        carImage = carImage2;
        return infoFragment;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* If in landscape, return */
        /*
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }
        */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            postponeEnterTransition();

        if(savedInstanceState == null) {

            CarInfoFragment carInfo = CarInfoFragment.newInstance(getIntent().getStringExtra(CAR_ID));
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, carInfo).addSharedElement(carImage, carImage.getTransitionName()).commit();
        }
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        //resultIntent.putExtra(IS_CHECKED_BOOLEAN, isFavoritedCheckBox.isChecked());
        //resultIntent.putExtra(CAR_ID, selectedCar.getCarID());
        setResult(1, resultIntent);
        finish();
    }
}
