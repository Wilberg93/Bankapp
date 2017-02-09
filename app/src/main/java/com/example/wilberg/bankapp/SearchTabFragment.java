package com.example.wilberg.bankapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.icu.text.LocaleDisplayNames;
import android.os.AsyncTask;
import android.os.Build;
import  android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wilberg.bankapp.Model.CarInfo;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */

public class SearchTabFragment extends Fragment {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";

    private String resultNumber;
    private int page;
    private int carOnPage;
    private int carNumber;
    private String sortMethod;
    private int row;
    private int rowsPerScrollUpdate;
    private View view;
    private TableLayout carTableScrollView;
    private android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView carScrollView;
    private ImageView carImageView;

    Button goToTopButton;
    TextView resultCount;

    ProgressDialog progressDialog;

    private ArrayList<CarInfo> cars = new ArrayList<CarInfo>();

    public SearchTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sortMethod = "";
        row = 0;
        carOnPage = 0;
        rowsPerScrollUpdate = 30;

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_result, container, false);
        resultCount = (TextView) view.findViewById(R.id.resultCountTextView);

        Intent theIntent = getActivity().getIntent();
        sortMethod = theIntent.getStringExtra("sortMethod");
        page = Globals.getInstance().getPage();

        /*
        nextPageButton = (Button) view.findViewById(R.id.nextPageButton);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                page ++;
                Globals.getInstance().updatePage(page);
                Intent theIntent = new Intent(getActivity(), MainTabActivity.class);
                startActivity(theIntent);

            }
        });
        */
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        //swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                clearCarScrollTable();
                new MyAsyncTask(false).execute();
                setRetainInstance(true);

            }
        });
        carTableScrollView = (TableLayout) view.findViewById(R.id.carTableScrollView);
        carScrollView = (ScrollView) view.findViewById(R.id.carScrollView);
        goToTopButton = (Button) view.findViewById(R.id.goToTopButton);
        goToTopButton.setVisibility(View.GONE);
        goToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        //scroll listener added for sdk 23+
        if (Build.VERSION.SDK_INT >= 23)
            carScrollView.setOnScrollChangeListener(onScrollListener);

        //scroll listener for sdk 22 and lower
        else {
            final ScrollView scrollView = (ScrollView) view.findViewById(R.id.carScrollView);
            final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
                    ViewTreeObserver.OnScrollChangedListener() {

                        @Override
                        public void onScrollChanged() {

                            int scrollY = scrollView.getScrollY();
                            if(scrollY == 0) {
                                goToTopButton.setVisibility(View.GONE);
                                swipeRefreshLayout.setEnabled(true);
                            }
                            else{
                                goToTopButton.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setEnabled(false);
                            }
                            View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                            Log.d("Y", Float.toString(diff));
                            if(diff < 720)
                                prepareInflation();

                        }
                    };


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
        }

        new MyAsyncTask(true).execute();
        setRetainInstance(true);

        return view;
    }


    @SuppressLint("NewApi")
    public View.OnScrollChangeListener onScrollListener = new View.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                Log.d("bottom", Integer.toString(carScrollView.getBottom()));
                Log.d("bottom", Integer.toString(i));

        }
    };
    private class MyAsyncTask extends AsyncTask<String, String, String> {

        boolean showProgressBar;
        public MyAsyncTask(Boolean showProgressBar) {
            super();
            this.showProgressBar = showProgressBar;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Searching...");
            progressDialog.setIndeterminate(false);
            if(showProgressBar)
                progressDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String url = "http://m.finn.no/car/used/search.html?filters&sort=" + sortMethod + "&page="+ page;
                Document doc = Jsoup.connect(url).get();
                resultNumber = doc.select("div.flex-grow1.rightify").select("b").first().ownText();

                int count = 0;
                int carNumber = page * 50;
                while (count <= 49) {

                    Element outerClass = doc.select("div.line.flex.align-items-stretch.wrap.cols1upto480.cols2upto990.cols3from990")
                            .select("div.unit.flex.align-items-stretch.result-item").get(count);

                    String carId = outerClass.select("a").first().attr("id");
                    String carImgURL = outerClass.select("img").first().attr("src");

                    Element carLocation = outerClass.select("span.licorice.valign-middle").get(0);
                    Element carTitle = outerClass.select("h3.t4.word-break.mhn.result-item-heading").get(0);

                    Element carInfoElement = outerClass.select("p.t5.word-break.mhn").get(0);
                    Element carYear = carInfoElement.select("span.prm").get(0);
                    Element carDistance = carInfoElement.select("span.prm").get(1);
                    Element carPrice = carInfoElement.select("span.prm").get(2);

                    CarInfo theCar = new CarInfo(null, carId, carTitle.ownText(),
                            carYear.ownText(), carDistance.ownText(), carPrice.ownText(), null, null, null, null, null, carLocation.ownText(), carImgURL, null, null, null);

                    cars.add(theCar);
                    count++;

                }

                Globals.getInstance().updateCars(cars);

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

        int startResult = page*10+1;
        int endResult = page*10+10;
        resultCount.setText(getString(R.string.search_result_text_view, resultNumber));
        prepareInflation();
        progressDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);

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

    public String getResultCount() {
        return resultNumber;
    }

    public void clearCarScrollTable() {

        carTableScrollView.removeAllViews();

    }

    private void inflateScrollView(CarInfo car) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newCarRow = inflater.inflate(R.layout.search_result_row_2, null);
        TextView carInfoTextView = (TextView) newCarRow.findViewById(R.id.carInfoTextView);
        TextView locationTextView = (TextView) newCarRow.findViewById(R.id.locationTextView);
        TextView priceTextView = (TextView) newCarRow.findViewById(R.id.priceTextView);
        TextView distanceTextView = (TextView) newCarRow.findViewById(R.id.distanceTextView);
        TextView yearTextView = (TextView) newCarRow.findViewById(R.id.yearTextView);
        TableRow inflatedTableRow = (TableRow) newCarRow.findViewById(R.id.tableRow1);
        Log.d("CARNR", Integer.toString(Globals.getInstance().getCars().size()));
        carImageView = (ImageView) newCarRow.findViewById(R.id.carImageView);
        Picasso.with(getActivity()).load(car.getMainImgURL()).into(carImageView);

        carInfoTextView.setText(car.getName());
        yearTextView.append(" " + car.getYear());
        distanceTextView.setText(car.getDistance());
        locationTextView.setText(" " + car.getLocation());
        priceTextView.setText(getString(R.string.actual_price_text_view, car.getPrice()));

        newCarRow.setTag(car.getCarID());
        carTableScrollView.addView(newCarRow);

        inflatedTableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent carIntent = new Intent(getActivity(), CarInfoActivity.class);
                carIntent.putExtra(CAR_ID, view.getTag().toString());

                startActivityForResult(carIntent, 1);

            }
        });

    }

}
