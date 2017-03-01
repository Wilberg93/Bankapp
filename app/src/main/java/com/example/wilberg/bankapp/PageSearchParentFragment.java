package com.example.wilberg.bankapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PageSearchParentFragment extends Fragment implements PageSearchChildInputFragment.OnFindButtonClickedListener, PageSearchChildResultFragment.OnRowClickedListener{

    ImageView mProductImage;

    PageSearchChildResultFragment childFragment;
    private ViewGroup mRootView;

    public PageSearchParentFragment() {
        // Required empty public constructor
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
    }

    public void insertChildFragment() {
        PageSearchChildInputFragment childFragment = new PageSearchChildInputFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.child_fragment_container, childFragment).commit();
    }

    @Override
    public void onInputReady(String inputValue) {
        childFragment = PageSearchChildResultFragment.newInstance("1000", "0");
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.child_fragment_container, childFragment).commit();
    }

    @Override
    public void onRowClicked(String selectedCarID, ImageView carImage) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));
                setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View mView = inflater.inflate(R.layout.fragment_page_search_child_result, null);
                mProductImage = carImage;


                        /* Launch a new Activity to show our DetailsFragment */
                CarInfoFragment fragment = CarInfoFragment.newInstance(0);

                FragmentTransaction ft = getFragmentManager().beginTransaction()
                        .addSharedElement(mProductImage, carImage.getTransitionName())
                        .replace(R.id.infoFragmentContainer, fragment)
                        .addToBackStack("transaction");
                ft.commit();
                // FragmentTransaction ft = getFragmentManager().beginTransaction().addSharedElement(carImage, "carImage");
                //ft.replace(R.id.infoFragmentContainer, carInfoFragment)
                //       .addToBackStack("transaction");
                //ft.commit();


        } else {
            /* TODO:Code for devices older than LOLLIPOP */
        }
    }

    @Override
    public void onUISetup(String carID) {
        CarInfoFragment carInfoFragment = CarInfoFragment.newInstance(0);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.infoFragmentContainer, carInfoFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
