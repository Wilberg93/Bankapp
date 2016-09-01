package com.example.wilberg.bankapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


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

            //Starts the carSearchActivity.
            Intent intent = new Intent(MainActivity.this, MainTabActivity.class);
            intent.putExtra("sortMethod", "0");
            startActivity(intent);

        }
    };
}
