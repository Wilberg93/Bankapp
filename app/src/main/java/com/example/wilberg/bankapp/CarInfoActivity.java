package com.example.wilberg.bankapp;

/**
 * Created by WILBERG on 8/27/2016.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.wilberg.bankapp.DB.DBTools;
import com.example.wilberg.bankapp.Model.CarInfo;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CarInfoActivity extends AppCompatActivity{

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String IS_CHECKED_BOOLEAN = "com.example.wilberg.bankapp.IS_CHECKED_BOOLEAN";

    private String carID;
    CarInfo selectedCar;


    DBTools dbTools;

    private TextView carTitleTextView;
    private TextView detailedTitleTextView;
    private TextView carPriceTextView;
    private TextView carDescriptionTextView;
    private CheckBox isFavoritedCheckBox;
    private ViewPager viewPager;
    private TextView pageDisplayTextView;
    private TableLayout specsTableLayout;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_info);

        progressBar = (ProgressBar) findViewById(R.id.progressbar_downloading);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        carTitleTextView = (TextView) findViewById(R.id.carTitleTextView);
        detailedTitleTextView = (TextView) findViewById(R.id.detailedTitleTextView);
        carPriceTextView = (TextView) findViewById(R.id.carPriceTextView);
        pageDisplayTextView = (TextView) findViewById(R.id.pageDisplayTextView);
        carDescriptionTextView = (TextView) findViewById(R.id.carDescriptionTextView);
        isFavoritedCheckBox = (CheckBox) findViewById(R.id.isFavoritedCheckBox);

        specsTableLayout = (TableLayout) findViewById(R.id.specsTableLayout);

        Intent intent = getIntent();
        carID = intent.getStringExtra(CAR_ID);
        dbTools = DBTools.getInstance(this);

        selectedCar = dbTools.getFavCar(carID);
        if(selectedCar == null) {
            for (CarInfo car : Globals.getInstance().getCars()) {
                if (car.getCarID().equals(carID)) {
                    selectedCar = car;
                    break;
                }
            }
        }

        if(isFavorited(selectedCar)) {
            isFavoritedCheckBox.setChecked(true);
        }
        isFavoritedCheckBox.setOnCheckedChangeListener(isFavoritedListener);


        new MyAsyncTask().execute(selectedCar);
    }

    private class MyAsyncTask extends AsyncTask<CarInfo, String, CarInfo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);


        }

        @Override
        protected CarInfo doInBackground(CarInfo... car) {
            LinkedHashMap<String, String> specs;
            ArrayList<String> imgURLs;
            try {
                CarInfo selectedCar = car[0];
                String url = "https://m.finn.no/car/used/ad.html?finnkode=" + selectedCar.getCarID();
                Document doc = Jsoup.connect(url).get();

                //various info on item
                String title = doc.select("h1.h1.word-break.mbn").first().ownText();
                String detailedTitle = doc.select("p.tcon.mtn").first().ownText();
                String carDescription = doc.select("div.mbl.object-description").select("p").first().ownText();
                Log.d("carDescription", carDescription);
                Log.d("detailedTitle", detailedTitle);

                //specs
                specs = new LinkedHashMap<>();
                Element specOuter = doc.select("dl.r-prl.mhn.multicol.col-count1upto640.col-count2upto768.col-count1upto990.col-count2from990").get(1);
                Element specKeyLast = specOuter.select("dt").last();
                Element specValueLast = specOuter.select("dd").last();
                Element specKey = specOuter.select("dt").get(0);
                Element specValue = specOuter.select("dd").get(0);
                int specTableRow = 0;
                while(!specKeyLast.equals(specKey)) {

                    specKey = specOuter.select("dt").get(specTableRow);
                    specValue = specOuter.select("dd").get(specTableRow);
                    specs.put(specKey.ownText(), specValue.ownText());
                    specTableRow++;
                }

                //images
                imgURLs = new ArrayList<>();
                Element outerClass = doc.select("div.line").first();
                //first image
                imgURLs.add(outerClass.select("img").get(0).attr("src"));

                int counter = 1;
                int numberOfImgs = Integer.parseInt(outerClass.select("img.centered-image").last().attr("data-index"));
                while(counter <= numberOfImgs) {
                    imgURLs.add(outerClass.select("img.centered-image").get(counter).attr("data-src"));
                    counter++;
                }
                selectedCar.setTitle(title);
                selectedCar.setImgURLs(imgURLs);
                selectedCar.setSpecs(specs);
                selectedCar.setDescription(carDescription);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return selectedCar;
        }

        @Override
        protected void onPostExecute(CarInfo selectedCar) {
            setUpUI(selectedCar);

        }

        public void setUpUI(CarInfo selectedCar) {

            carTitleTextView.setText(selectedCar.getTite());
            detailedTitleTextView.setText(selectedCar.getName());
            carPriceTextView.setText(getString(R.string.actual_price_text_view, selectedCar.getPrice()));
            carDescriptionTextView.setText(selectedCar.getDescription());

            for (Map.Entry<String,String> spec : selectedCar.getSpecs().entrySet())
                inflateScrollView(spec.getKey(), spec.getValue());

            progressBar.setVisibility(View.GONE);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            ImagePagerAdapter adapter = new ImagePagerAdapter(selectedCar);
            final int totalImages = adapter.getCount();
            pageDisplayTextView.setText(getString(R.string.image_result_default, totalImages));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int currentState;
                int currentPosition = 0;
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    pageDisplayTextView.setText((getString(R.string.image_result_on_page_changed, position+1, totalImages)));
                    currentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    handleScrollState(state);
                }
                private void handleScrollState(final int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        setNextItemIfNeeded();
                    }
                }

                private void setNextItemIfNeeded() {
                    if (!isScrollStateSettling()) {
                        handleSetNextItem();
                    }
                }

                private boolean isScrollStateSettling() {
                    return currentState == ViewPager.SCROLL_STATE_SETTLING;
                }

                private void handleSetNextItem() {
                    final int lastPosition = viewPager.getAdapter().getCount() - 1;
                    if(currentPosition == 0) {
                        viewPager.setCurrentItem(lastPosition, false);
                    } else if(currentPosition == lastPosition) {
                        viewPager.setCurrentItem(0, false);
                    }
                }
            });
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(selectedCar.getImgURLs().size());
        }

    }
    private class ImagePagerAdapter extends PagerAdapter {

        CarInfo selectedCar;


        public ImagePagerAdapter(CarInfo selectedCar) {
            this.selectedCar = selectedCar;
        }

        @Override
        public int getCount() {
            if(selectedCar.getImgURLs() != null)
                return selectedCar.getImgURLs().size();
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = CarInfoActivity.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_medium);
            imageView.setPadding(padding, 0, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Picasso.with(context).load(selectedCar.getImgURLs().get(position)).into(imageView);
            (container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    public void inflateScrollView(String specKey, String specValue) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newSpecsRow = inflater.inflate(R.layout.car_info_specs_row, null);
        TextView specKeyTextView = (TextView) newSpecsRow.findViewById(R.id.specKeyTextView);
        TextView specValueTextView = (TextView) newSpecsRow.findViewById(R.id.specValueTextView);

        specKeyTextView.setText(specKey);
        specValueTextView.setText(specValue);

        specsTableLayout.addView(newSpecsRow);

    }

    public OnCheckedChangeListener isFavoritedListener = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

            if (isChecked) {
                if(isFavorited(selectedCar))
                    return;
                Globals.getInstance().addFavoritedCar(selectedCar);
                dbTools.insertFavCar(selectedCar);
            }
            else {
                dbTools.removeFavCar(selectedCar);
                /*
                for(CarInfo theCar: Globals.getInstance().getFavoritedCars()) {
                    if(theCar.equals(selectedCar)) {
                        Globals.getInstance().removeFavoritedCar(selectedCar);
                        dbTools.removeFavCar(selectedCar);
                        break;
                    }
                }
                */
            }
        }
    };

    public boolean isFavorited(CarInfo selectedCar) {
        return dbTools.checkForCar(selectedCar.getCarID());
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(IS_CHECKED_BOOLEAN, isFavoritedCheckBox.isChecked());
        resultIntent.putExtra(CAR_ID, selectedCar.getCarID());
        setResult(1, resultIntent);
        finish();
    }
}