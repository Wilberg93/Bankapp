package com.example.wilberg.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CarSearchActivity extends AppCompatActivity {
	
	public final static String CALC_VALUE = "com.wilberg.bankapp.CALCVALUE";
	
	Button findCarsButton;
	
	private EditText equityEditText;
	private EditText perMonthEditText;
	private EditText repaymentEditText;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_search);
        
        equityEditText = (EditText) findViewById(R.id.equityEditText);
    	perMonthEditText = (EditText) findViewById(R.id.perMonthEditText);
    	repaymentEditText = (EditText) findViewById(R.id.repaymentEditText);
        
        findCarsButton = (Button) findViewById(R.id.findCarsButton);
        
        findCarsButton.setOnClickListener(findCarsButtonListener);
        
        equityEditText.addTextChangedListener(equityTextWatcher);
        
    }

    public OnClickListener findCarsButtonListener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {

			String calcValue = calculateValue();
			/*
			Intent intent = new Intent(CarSearchActivity.this, MainTabActivity.class);
			intent.putExtra(CALC_VALUE, calcValue);
			startActivity(intent);
			*/
		}

    };
    
    public TextWatcher equityTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable text) {

			text.toString().replace(" ", "");
			if (text.length() == 4)
				equityEditText.getText().insert(equityEditText.getText().length() -3, " ");
			if (text.length() > 4 && text.length() %4 == 1)
				equityEditText.getText().insert(equityEditText.getText().length(), " ");

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) { }

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) { }

    };
    
    public String calculateValue(){
    	
    	String equity = equityEditText.getText().toString();
    	String perMonth = perMonthEditText.getText().toString();
    	String repayment = repaymentEditText.getText().toString();

		//Temp return value
    	return "10000";

    }
}
