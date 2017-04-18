package com.example.wilberg.bankapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PageSearchParentFragment extends Fragment implements PageSearchChildInputFragment.OnFindButtonClickedListener, PageSearchChildResultFragment.OnRowClickedListener,
        OnConnectionErrorListener, ConnectionErrorFragment.OnReconnectListener{

    private PageSearchChildResultFragment mChildResultFragment;

        public PageSearchParentFragment() {
            // Required empty public constructor.
        }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_search_parent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        insertChildFragment();
        //TODO: Handle dual pane.
        if(false) displayFirstCar("");
    }

    public void insertChildFragment() {
        // Add input child fragment to the container.
        PageSearchChildInputFragment childInputFragment = new PageSearchChildInputFragment();
        getChildFragmentManager().beginTransaction()
                .add(R.id.childFragmentContainer, childInputFragment)
                .addToBackStack("input")
                .commit();
    }

    public void updateSortMethod(String sortMethod) {
        // Update sort method.
        mChildResultFragment = PageSearchChildResultFragment.newInstance("1000", sortMethod);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.childFragmentContainer, mChildResultFragment)
                .commit();
    }

    @Override
    public void onInputReady(String calculatedPriceLimit) {
        // Instantiate PageSearchChildResultFragment with calculated value.
        mChildResultFragment = PageSearchChildResultFragment.newInstance(calculatedPriceLimit, "0");
        getChildFragmentManager().beginTransaction()
                .replace(R.id.childFragmentContainer, mChildResultFragment)
                .addToBackStack("result")
                .commit();
    }

    @Override
    public void onRowClicked(String selectedCarID, ImageView carImage) {
        // Called when car in recyclerView is clicked and device is in dual pane.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Start transaction with material transition.

            //setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));
            //setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));

            CarInfoFragment fragment = CarInfoFragment.newInstance(getFragmentManager(),0);

            FragmentTransaction ft = getFragmentManager().beginTransaction()
                    .addSharedElement(carImage, carImage.getTransitionName())
                    .replace(R.id.infoFragmentContainer, fragment)
                    .addToBackStack("CarInfoTransaction");
            ft.commit();

        } else {
            /* TODO:Code for devices older than LOLLIPOP */
        }
    }

    public void displayFirstCar(String carID) {
        CarInfoFragment carInfoFragment = CarInfoFragment.newInstance(getFragmentManager(), 0);

        getFragmentManager().beginTransaction()
                .replace(R.id.infoFragmentContainer, carInfoFragment)
                .commit();
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    public void onActivityReenter(Intent data) {
        mChildResultFragment.handleActivityReenter(data);
    }

    @Override
    public void handleConnectionError() {
        ConnectionErrorFragment connectionErrorFragment = ConnectionErrorFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.childFragmentContainer, connectionErrorFragment)
                .addToBackStack("connectionError")
                .commit();
    }

    @Override
    public void onReconnected() {
        getFragmentManager().popBackStack();
    }
}