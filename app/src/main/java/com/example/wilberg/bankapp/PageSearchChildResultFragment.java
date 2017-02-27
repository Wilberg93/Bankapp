package com.example.wilberg.bankapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wilberg.bankapp.Model.Car;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by WILBERG on 2/18/2017.
 */
public class PageSearchChildResultFragment extends Fragment{

    private OnRowClickedListener listener;

    public interface OnRowClickedListener {
        void onRowClicked(String inputValue, ImageView carImage);
        void onUISetup(String carID);
    }

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String INPUT_VALUE = "com.example.wilberg.bankapp.INPUT_VALUE";
    private final static String SORT_METHOD = "com.example.wilberg.bankapp.SORT_METHOD";

    private String resultNumber;
    private int page;
    private int carOnPage;
    private String sortMethod;
    private int row;
    private int resultsWanted;
    private View view;
    private NestedScrollView carScrollView;
    private TableLayout carTableScrollView;
    private android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    FloatingActionButton fab;
    Button goToTopButton;
    TextView resultCount;
    ProgressDialog progressDialog;
    Toolbar mToolbar;
    int count = 0;
    boolean mDualPane;

    private ArrayList<Car> cars = new ArrayList<Car>();

    /*Use newInstance to pass arguments to fragment*/
    public static PageSearchChildResultFragment newInstance(String inputValue, String sortMethod) {
        Bundle args = new Bundle();
        args.putString(INPUT_VALUE, inputValue);
        args.putString(SORT_METHOD, sortMethod);
        PageSearchChildResultFragment fragment = new PageSearchChildResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getParentFragment() instanceof OnRowClickedListener)
            listener = (OnRowClickedListener) getParentFragment();
        else
            throw new ClassCastException(context.toString() + " must implement OnFindButtonClickedListener or OnUISetup");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortMethod = "0";
        row = 0;
        carOnPage = 0;
        resultsWanted = 9;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Handle Landscape mode */
        View infoFragmentContainer = getParentFragment().getActivity().findViewById(R.id.infoFragmentContainer);
        mDualPane = (infoFragmentContainer != null) && (infoFragmentContainer.getVisibility() == View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_search_child_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /* Get passed arguments */
        sortMethod = getArguments().getString(SORT_METHOD);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        resultCount = (TextView) view.findViewById(R.id.resultCountTextView);

        page = Globals.getInstance().getPage();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                clearCarScrollTable();
                new MyAsyncTask(false).execute();
                setRetainInstance(true);

            }
        });
        carScrollView = (NestedScrollView) view.findViewById(R.id.carScrollView);
        carTableScrollView = (TableLayout) view.findViewById(R.id.carTableScrollView);

        //scroll listener added for sdk 23+
        /*
        if (Build.VERSION.SDK_INT >= 23)
            carScrollView.setOnScrollChangeListener(onScrollListener);

        //scroll listener for sdk 22 and lower
        else {
        */
        /* Handle swipeRefreshLayout */
        final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.carScrollView);
        final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
                ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {

                        int scrollY = scrollView.getScrollY();
                        if(scrollY == 0)
                            swipeRefreshLayout.setEnabled(true);
                        else
                            swipeRefreshLayout.setEnabled(false);
                        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                        if(diff < 720)
                            prepareInflation();
                    }
                };

        /* When reached bottom, inflate more rows */
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private ViewTreeObserver observer;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (observer == null) {
                    observer = scrollView.getViewTreeObserver();
                    observer.addOnScrollChangedListener(onScrollChangedListener);
                }
                else if (!observer.isAlive()) {
                    observer.removeOnScrollChangedListener(onScrollChangedListener);
                    observer = scrollView.getViewTreeObserver();
                    observer.addOnScrollChangedListener(onScrollChangedListener);
                }

                return false;
            }
        });
        // }
        new MyAsyncTask(true).execute();
        setRetainInstance(true);
    }
    /*
        @SuppressLint("NewApi")
        public View.OnScrollChangeListener onScrollListener = new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

            }
        };
        */
    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        boolean showProgressBar;
        public MyAsyncTask(Boolean showProgressBar) {
            super();
            this.showProgressBar = showProgressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(R.style.CustomProgressBar);
            progressDialog.setMessage(getString(R.string.progress_message));
            progressDialog.setIndeterminate(false);
           /* if(showProgressBar)
                progressDialog.show();
                */
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                /* Pull data from URL */
                String url = "http://m.finn.no/car/used/search.html?filters&sort=" + sortMethod + "&page="+ page;
                Document doc = Jsoup.connect(url).get();
                resultNumber = doc.select("div.flex-grow1.rightify").select("b").first().ownText();

                int count = 0;
                while (count <= resultsWanted) {

                    Element outerClass = doc.select("div.line.flex.align-items-stretch.wrap.cols1upto480.cols2upto990.cols3from990")
                            .select("div.unit.flex.align-items-stretch.result-item").get(count);

                    String carId = outerClass.select("a").first().attr("id");
                    String carImgURL = outerClass.select("img").first().attr("src");

                    Element carLocation = outerClass.select("span.licorice.valign-middle").get(0);
                    Element carTitle = outerClass.select("h3.t4.word-break.mhn.result-item-heading").get(0);

                    Element carInfoElement = outerClass.select("p.t5.word-break.mhn").get(0);
                    Element carYear = carInfoElement.select("span.prm").get(0);
                    Element carDistance = carInfoElement.select("span.prm").get(1);
                    Element carPrice = carInfoElement.select("span.prm").get(1);
                    /* Create new car object with pulled data*/
                    Car theCar = new Car(carTitle.ownText(), carId, carTitle.ownText(),
                            carYear.ownText(), carDistance.ownText(), carPrice.ownText(), null, null, null, null, null, carLocation.ownText(), carImgURL, null, null, null);
                    cars.add(theCar);
                    count++;

                } Globals.getInstance().updateCars(cars);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            setUpUI();
        }
    }

    public void setUpUI() {

        resultCount.setText(getString(R.string.search_result_text_view, resultNumber));
        prepareInflation();
        if(mDualPane)
            displayFirstCar(Globals.getInstance().getCars().get(0));
        progressDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);

    }

    private void displayFirstCar(Car car) {
        listener.onUISetup(car.getCarID());
    }

    public void prepareInflation() {
        if(carOnPage == 50) {
            carOnPage = 0;
            page++;
            new MyAsyncTask(false).execute();
            setRetainInstance(true);
        }
        else {
            int count = 0;
            for (int i = row; i < Globals.getInstance().getCars().size() && count < 9; i++) {
                inflateScrollView(Globals.getInstance().getCars().get(i));
                row++;
                count++;
                carOnPage++;
            }
        }
    }

    public void setSortMethod(String sortMethod) { this.sortMethod = sortMethod; }

    public void clearCarScrollTable() {
        carTableScrollView.removeAllViews();
    }

    private void inflateScrollView(Car car) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View newCarRow = inflater.inflate(R.layout.search_result_row_wide, null);
        TextView carInfoTextView = (TextView) newCarRow.findViewById(R.id.carInfoTextView);
        TextView locationTextView = (TextView) newCarRow.findViewById(R.id.locationTextView);
        TextView priceTextView = (TextView) newCarRow.findViewById(R.id.priceTextView);
        TextView distanceTextView = (TextView) newCarRow.findViewById(R.id.distanceTextView);
        TextView yearTextView = (TextView) newCarRow.findViewById(R.id.yearTextView);
        TableRow inflatedTableRow = (TableRow) newCarRow.findViewById(R.id.tableRow1);
        final ImageView carImageView = (ImageView) newCarRow.findViewById(R.id.carImageView);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            carImageView.setTransitionName("info" + car.getCarID());
        Picasso.with(getActivity()).load(car.getMainImgURL()).fit().into(carImageView);

        carInfoTextView.setText(car.getName());
        yearTextView.append(car.getYear());
        distanceTextView.setText(car.getDistance());
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
                    if (mDualPane) {

                        listener.onRowClicked(selectedCarID, carImageView);

                    } else {



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));
                            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View mView = inflater.inflate(R.layout.fragment_page_search_child_result, null);


                        /* Launch a new Activity to show our DetailsFragment */
                            CarInfoFragment fragment = CarInfoFragment.newInstance(selectedCarID);
                            fragment.setTransitionName(carImageView.getTransitionName());
                            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
                            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                            fragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
                            fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                            //fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));


                            Log.d(carImageView.getTransitionName(), fragment.gettTransitionName());
                            /*
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                                    .addSharedElement(carImageView, carImageView.getTransitionName())
                                    .replace(android.R.id.content, fragment)
                                    .addToBackStack("transaction");
                            ft.commit();
                            */



                        } else {
            /* TODO:Code for devices older than LOLLIPOP */
                        };

                        //TEST CODE
                        CarInfoActivity mActivity = CarInfoActivity.newInstance(selectedCarID, carImageView);



                        Intent intent = new Intent();

                        /* Define the class Activity to call */
                            intent.setClass(getActivity(), CarInfoActivity.class);


                        /* Pass along the currently selected index assigned to the keyword index */
                            //intent.putExtra(CAR_ID, selectedCarID);

                        /* Call for the Activity to open */
                            //startActivity(intent);

                    }
                }
            }
        });
    }
}