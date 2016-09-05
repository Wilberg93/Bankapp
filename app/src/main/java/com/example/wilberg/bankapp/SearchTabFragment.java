package com.example.wilberg.bankapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import  android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

    private String resultNumber;
    private int page;
    private int carNumber;
    private String sortMethod = "";

    View view;
    private TableLayout carTableScrollView;
    ImageView carImageView;

    Button testButton;
    Button nextPageButton;
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

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_result, container, false);
        resultCount = (TextView) view.findViewById(R.id.resultCountTextView);

        Intent theIntent = getActivity().getIntent();
        sortMethod = theIntent.getStringExtra("sortMethod");
        page = Globals.getInstance().getPage();

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

        carTableScrollView = (TableLayout) view.findViewById(R.id.carTableScrollView);

        new MyAsyncTask().execute();
        setRetainInstance(true);

        return view;
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Searching...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String url = "http://m.finn.no/car/used/search.html?filters&sort=" + sortMethod;
                Document doc = Jsoup.connect(url).get();
                resultNumber = doc.select("div.flex-grow1.rightify").select("b").first().ownText();

                int count = 0;
                int carNumber = page * 10;
                while (count <= 9) {

                    Element outerClass = doc.select("div.line.flex.align-items-stretch.wrap.cols1upto480.cols2upto990.cols3from990")
                            .select("div.unit.flex.align-items-stretch.result-item").get(carNumber + count);

                    String carId = outerClass.select("a").first().attr("id");
                    String carImgURL = outerClass.select("img").first().attr("src");

                    Element carLocation = outerClass.select("span.opaque.valign-middle").get(0);
                    Element carTitle = outerClass.select("h3.t4.word-break.mhn.result-item-heading").get(0);

                    Element carInfoElement = outerClass.select("p.t5.word-break.mhn").get(0);
                    Element carYear = carInfoElement.select("span.prm").get(0);
                    Element carDistance = carInfoElement.select("span.prm").get(1);
                    Element carPrice = carInfoElement.select("span.prm").get(2);

                    CarInfo theCar = new CarInfo(Integer.toString(count), carId, carTitle.ownText(),
                            carYear.ownText(), carDistance.ownText(), carPrice.ownText(), null, null, null, null, null, carLocation.ownText(), carImgURL);

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
            progressDialog.dismiss();
            setUpUI();
        }
    }

    public void setUpUI() {

        resultCount.setText("Showing 10 of " + resultNumber + " results");
        for (CarInfo car: Globals.getInstance().getCars()) {
            inflateScrollView(car);
        }

    }

    public void setSortMethod(String sortMethod) {

        this.sortMethod = sortMethod;

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
