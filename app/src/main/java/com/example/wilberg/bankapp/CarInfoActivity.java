package com.example.wilberg.bankapp;

/**
 * Created by WILBERG on 8/27/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wilberg.bankapp.Model.CarInfo;
import com.squareup.picasso.Picasso;

public class CarInfoActivity extends AppCompatActivity{

    private int rowId;
    CarInfo selectedCar;

    private TextView carNameTextView;
    private CheckBox isFavoritedCheckBox;
    ImageView carImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_info);

        carNameTextView = (TextView) findViewById(R.id.carNameTextView);
        isFavoritedCheckBox = (CheckBox) findViewById(R.id.isFavoritedCheckBox);

        carImageView = (ImageView) findViewById(R.id.carImageView);

        Intent intent = getIntent();
        rowId = intent.getIntExtra("carId", -1);
        selectedCar = Globals.getInstance().getCars().get(rowId);

        Picasso.with(this).load(selectedCar.getImgURL()).into(carImageView);

        carNameTextView.setText(selectedCar.getName());

        if(containsCar(selectedCar)) {
            isFavoritedCheckBox.setChecked(true);
        }

        isFavoritedCheckBox.setOnCheckedChangeListener(isFavoritedListener);

    }

    public OnCheckedChangeListener isFavoritedListener = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

            if (isChecked) {

                if(containsCar(selectedCar))
                    return;
                Globals.getInstance().addFavoritedCar(selectedCar);

            }
            else {

                for(CarInfo theCar: Globals.getInstance().getFavoritedCars()) {

                    if(theCar.equals(selectedCar)) {
                        Globals.getInstance().removeFavoritedCar(selectedCar);
                        break;
                    }
                }
            }
        }
    };

    public boolean containsCar(CarInfo selectedCar) {
        Log.d("checkBox", "checkbox");
        return Globals.getInstance().getFavoritedCars().contains(selectedCar);
    }

    @Override
    public void onBackPressed() {

        Intent resultIntent = new Intent();
        setResult(1, resultIntent);
        finish();

    }

}