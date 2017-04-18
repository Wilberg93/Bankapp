package com.example.wilberg.bankapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wilberg.bankapp.Util.NetworkUtils;

/**
 * Created by WILBERG on 3/11/2017.
 */
public class ConnectionErrorFragment extends Fragment implements View.OnClickListener{

    public OnReconnectListener mReconnectListener;

    public interface OnReconnectListener {
        void onReconnected();
    }

    public static ConnectionErrorFragment newInstance() {
        ConnectionErrorFragment fragment = new ConnectionErrorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getParentFragment() instanceof OnReconnectListener) mReconnectListener = (OnReconnectListener) getParentFragment();
        else
            throw new ClassCastException(context.toString() + " must implement OnReconnectListener");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection_error, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.reloadTextView).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mReconnectListener.onReconnected();
    }
}
