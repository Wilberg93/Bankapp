package com.example.wilberg.bankapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by WILBERG on 8/19/2016.
 */
public class SortFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TextView sortView = new TextView(getActivity());
        sortView.setText("HAHAAHAH");
        return sortView;
    }

}
