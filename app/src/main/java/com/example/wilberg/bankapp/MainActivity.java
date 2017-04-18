package com.example.wilberg.bankapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.carButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        //Starts the Tab activity
        Intent intent = new Intent(MainActivity.this, TabsActivity.class);
        startActivity(intent);
    }
}