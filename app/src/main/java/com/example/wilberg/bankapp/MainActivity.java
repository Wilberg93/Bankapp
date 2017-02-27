package com.example.wilberg.bankapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.wilberg.bankapp.DB.DBTools;


public class MainActivity extends AppCompatActivity {

    Button carButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carButton = (Button) findViewById(R.id.carButton);
        carButton.setOnClickListener(carButtonListener);
    }

    public OnClickListener carButtonListener = new OnClickListener(){

        @Override
        public void onClick(View arg0) {

            //DBTools.getInstance(getApplicationContext()).dropDatabase();
            //Starts the Tab activity
            Intent intent = new Intent(MainActivity.this, TabsActivity.class);
            intent.putExtra("sortMethod", "0");
            startActivity(intent);

        }
    };
}
