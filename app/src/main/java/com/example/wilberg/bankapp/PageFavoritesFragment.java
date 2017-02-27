package com.example.wilberg.bankapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wilberg.bankapp.DB.DBTools;
import com.example.wilberg.bankapp.Model.Car;
import com.squareup.picasso.Picasso;

public class PageFavoritesFragment extends Fragment {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";

    View view;
    private TableLayout carTableScrollView;
    ImageView carImageView;

    boolean mDuelPane;

    public PageFavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Handle Landscape mode */
        View infoFragmentContainer = getActivity().findViewById(R.id.infoFragmentContainer);
        mDuelPane = (infoFragmentContainer != null) && (infoFragmentContainer.getVisibility() == View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carTableScrollView = (TableLayout) view.findViewById(R.id.carTableScrollView);
        setUpUI();
    }

    public void setUpUI() {
        carTableScrollView.removeAllViews();
        for(Car theCar: DBTools.getInstance(getContext()).getFavCars())
            inflateScrollView(theCar);
    }

    private void inflateScrollView(Car car) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newCarRow = inflater.inflate(R.layout.search_result_row, null);

        TextView carInfoTextView = (TextView) newCarRow.findViewById(R.id.carInfoTextView);
        TextView locationTextView = (TextView) newCarRow.findViewById(R.id.locationTextView);
        TextView priceTextView = (TextView) newCarRow.findViewById(R.id.priceTextView);
        TextView distanceTextView = (TextView) newCarRow.findViewById(R.id.distanceTextView);
        TextView yearTextView = (TextView) newCarRow.findViewById(R.id.yearTextView);
        TableRow inflatedTableRow = (TableRow) newCarRow.findViewById(R.id.tableRow1);

        carImageView = (ImageView) newCarRow.findViewById(R.id.carImageView);
        Picasso.with(getActivity()).load(car.getMainImgURL()).fit().into(carImageView);

        carInfoTextView.setText(car.getName());
        yearTextView.append(car.getYear());
        distanceTextView.append(car.getDistance());
        locationTextView.setText(car.getLocation());
        priceTextView.setText(getString(R.string.price_value_text_view, car.getPrice()));

        newCarRow.setTag(car.getCarID());
        carTableScrollView.addView(newCarRow);

        inflatedTableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CarInfoFragment carInfoFragment = (CarInfoFragment)getFragmentManager().findFragmentById(R.id.infoFragmentContainer);
                String selectedCarID = view.getTag().toString();
                /* If different row is clicked */
                if(carInfoFragment == null || !carInfoFragment.getCarID().equals(selectedCarID)) {
                    /* If in landscape mode */
                    if (mDuelPane) {
                        /* Pass carID to InfoFragment */
                        carInfoFragment = CarInfoFragment.newInstance(selectedCarID);

                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        ft.replace(R.id.infoFragmentContainer, carInfoFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    } else {
                        /* Launch a new Activity to show our DetailsFragment */
                        Intent intent = new Intent();

                        /* Define the class Activity to call */
                        intent.setClass(getActivity(), CarInfoActivity.class);

                        /* Pass along the currently selected index assigned to the keyword index */
                        intent.putExtra(CAR_ID, selectedCarID);

                        /* Call for the Activity to open */
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void updateView(String carID) {
        inflateScrollView(DBTools.getInstance(getContext()).getFavCar(carID));
    }

    public void removeView(String carID) {
        carTableScrollView.removeView(carTableScrollView.findViewWithTag(carID));

    }

}
