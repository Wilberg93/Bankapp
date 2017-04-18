package com.example.wilberg.bankapp;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class PageSearchChildInputFragment extends Fragment implements OnClickListener{

	private OnFindButtonClickedListener listener;

	public interface OnFindButtonClickedListener {
		void onInputReady(String inputValue);
	}
	
	public final static String EXTRA_CALC_VALUE = "com.wilberg.bankapp.EXTRA_CALCVALUE";
	
	private EditText equityEditText;
	private EditText perMonthEditText;
	private EditText repaymentEditText;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(getParentFragment() instanceof OnFindButtonClickedListener)
			listener = (OnFindButtonClickedListener) getParentFragment();
		else
			throw new ClassCastException(context.toString() + " must implement OnFindButtonClickedListener");
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_page_search_child_input, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Create enter transition
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Animator animator = ViewAnimationUtils.createCircularReveal(view, 0, 0, 0, Math.max(view.getWidth(), view.getHeight()));
			animator.start();
		}

		equityEditText = (EditText) view.findViewById(R.id.equityEditText);
		perMonthEditText = (EditText) view.findViewById(R.id.perMonthEditText);
		repaymentEditText = (EditText) view.findViewById(R.id.repaymentEditText);
		// TextWatcher.
		equityEditText.addTextChangedListener(equityTextWatcher);

		// Button click listeners.
		view.findViewById(R.id.findCarsButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		listener.onInputReady(calculateValue());
	}
    
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
    	// Calculate value to use as limit for search for cars.
    	String equity = equityEditText.getText().toString();
    	String perMonth = perMonthEditText.getText().toString();
    	String repayment = repaymentEditText.getText().toString();

		//Temp return value
    	return "10000";
    }
}