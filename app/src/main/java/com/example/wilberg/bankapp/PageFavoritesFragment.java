package com.example.wilberg.bankapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private boolean mDuelPane;

    private TableLayout mCarTableLayout;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCarTableLayout = (TableLayout) view.findViewById(R.id.carTableScrollView);

        //Inflate scrollView
        for(Car theCar: DBTools.getInstance(getContext()).getFavCars())
            inflateScrollView(theCar);
    }

    private void inflateScrollView(Car car) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newCarRow = inflater.inflate(R.layout.search_result_row, null);

        ((TextView) newCarRow.findViewById(R.id.carInfoTextView)).setText(car.getName());
        ((TextView) newCarRow.findViewById(R.id.locationTextView)).setText(car.getLocation());;
        ((TextView) newCarRow.findViewById(R.id.priceTextView)).setText(getString(R.string.price_value_text_view, car.getPrice()));
        ((TextView) newCarRow.findViewById(R.id.distanceTextView)).append(car.getDistance());
        ((TextView) newCarRow.findViewById(R.id.yearTextView)).append(car.getYear());

        ImageView carImageView = (ImageView) newCarRow.findViewById(R.id.carImageView);
        Picasso.with(getActivity()).load(car.getMainImgURL()).fit().into(carImageView);


        newCarRow.setTag(car.getCarID());
        mCarTableLayout.addView(newCarRow);

        newCarRow.findViewById(R.id.tableRow1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                CarInfoFragment carInfoFragment = (CarInfoFragment)getFragmentManager().findFragmentById(R.id.infoFragmentContainer);
                String selectedCarID = view.getTag().toString();
                //If different row is clicked
                if(carInfoFragment == null || !carInfoFragment.getCarID().equals(selectedCarID)) {
                    /* If in landscape mode
                    if (mDuelPane) {
                        // Pass carID to InfoFragment
                        carInfoFragment = CarInfoFragment.newInstance(selectedCarID);

                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        ft.replace(R.id.infoFragmentContainer, carInfoFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    } else {
                        // Launch a new Activity to show our DetailsFragment
                        Intent intent = new Intent();

                        /* Define the class Activity to call
                        intent.setClass(getActivity(), CarInfoActivity.class);

                        /* Pass along the currently selected index assigned to the keyword index
                        intent.putExtra(CAR_ID, selectedCarID);

                        /* Call for the Activity to open
                        startActivity(intent);
                    }
                }
                                */
            }
        });
    }

    public void updateView(String carID) {
        inflateScrollView(DBTools.getInstance(getContext()).getFavCar(carID));
    }

    public void removeView(String carID) {
        mCarTableLayout.removeView(mCarTableLayout.findViewWithTag(carID));

    }

}