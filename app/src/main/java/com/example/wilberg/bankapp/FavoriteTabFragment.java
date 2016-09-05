package com.example.wilberg.bankapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wilberg.bankapp.Model.CarInfo;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteTabFragment extends Fragment {

    View view;
    private TableLayout carTableScrollView;
    ImageView carImageView;

    public FavoriteTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_favorites_search, container, false);

        carTableScrollView = (TableLayout) view.findViewById(R.id.carTableScrollView);

        setUpUI();

        return view;

    }

    public void setUpUI() {

        carTableScrollView.removeAllViews();
        for(CarInfo theCar: Globals.getInstance().getFavoritedCars())
            inflateScrollView(theCar);

    }

    private void inflateScrollView(CarInfo car) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newCarRow = inflater.inflate(R.layout.search_result_row, null);
        TextView carInfoTextView = (TextView) newCarRow.findViewById(R.id.carInfoTextView);
        TextView locationTextView = (TextView) newCarRow.findViewById(R.id.locationTextView);
        TextView priceTextView = (TextView) newCarRow.findViewById(R.id.priceTextView);
        TextView modelTextView = (TextView) newCarRow.findViewById(R.id.modelTextView);
        TextView distanceTextView = (TextView) newCarRow.findViewById(R.id.distanceTextView);
        TextView yearTextView = (TextView) newCarRow.findViewById(R.id.yearTextView);
        TableRow inflatedTableRow = (TableRow) newCarRow.findViewById(R.id.tableRow1);
        inflatedTableRow.setId(Integer.parseInt(car.getRowId()));

        carImageView = (ImageView) newCarRow.findViewById(R.id.carImageView);
        Picasso.with(getActivity()).load(car.getImgURL()).into(carImageView);

        carInfoTextView.setText(car.getName());
        yearTextView.append(" " + car.getYear());
        distanceTextView.append(car.getDistance());
        locationTextView.setText(" " + car.getLocation());
        priceTextView.append(car.getPrice() + " kr");

        int rowId = carTableScrollView.getChildCount();
        newCarRow.setTag(rowId);
        carTableScrollView.addView(newCarRow);

        inflatedTableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent carIntent = new Intent(getActivity(), CarInfoActivity.class);

                carIntent.putExtra("carId", (Integer)view.getTag());
                startActivityForResult(carIntent, 1);

            }
        });

    }

}