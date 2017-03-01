package com.example.wilberg.bankapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.wilberg.bankapp.DB.DBTools;
import com.example.wilberg.bankapp.Model.Car;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CarInfoFragment extends Fragment {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String IS_CHECKED_BOOLEAN = "com.example.wilberg.bankapp.";

    private final Callback mImageCallback = new Callback() {
        @Override
        public void onSuccess() {
            Log.d("worksLEL", "works");
            startPostponedEnterTransition2();
        }

        @Override
        public void onError() {
            Log.d("worksLEL", "works");
            startPostponedEnterTransition2();
        }
    };

    private String carID;
    private Boolean mDuelPane = false;
    private String transitionName;
    Car selectedCar;

    DBTools dbTools;

    private View view;
    private TextView carTitleTextView;
    private TextView detailedTitleTextView;
    private TextView carPriceTextView;
    private TextView carDescriptionTextView;
    private CheckBox isFavoritedCheckBox;
    private FloatingActionButton favoriteFAB;
    private ViewPager viewPager;
    private TextView pageDisplayTextView;
    private TableLayout specsTableLayout;
    private ImageView launchIcon;
    ImageView carImageView;

    ProgressBar progressBar;

    /*Use newInstance to pass arguments to fragment*/
    public static CarInfoFragment newInstance(int position) {
        Bundle args = new Bundle();
        String carID = Globals.getInstance().getCars().get(position).getCarID();
        args.putString(CAR_ID, carID);
        CarInfoFragment infoFragment = new CarInfoFragment();
        infoFragment.setArguments(args);
        return infoFragment;
    }

    public String getCarID() {
        return getArguments().get(CAR_ID).toString();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View infoFragmentContainer = getActivity().findViewById(R.id.infoFragmentContainer);
        mDuelPane = (infoFragmentContainer != null) && (infoFragmentContainer.getVisibility() == View.VISIBLE);
        if(mDuelPane)
            launchIcon.setVisibility(View.VISIBLE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_info, container, false);

        carID = getCarID();
        dbTools = DBTools.getInstance(getContext());
        selectedCar = dbTools.getFavCar(carID);
        if(selectedCar == null) {
            for (Car car : Globals.getInstance().getCars()) {
                if (car.getCarID().equals(carID)) {
                    selectedCar = car;
                    break;
                }
            }
        }

        final ImageView backgroundImage = (ImageView) rootView.findViewById(R.id.details_background_image);
        carImageView = (ImageView) rootView.findViewById(R.id.carImageView);
        carTitleTextView = (TextView) rootView.findViewById(R.id.carTitleTextView);
        carImageView.setTransitionName("carImage" + carID);
        carTitleTextView.setTransitionName("carTitle" + selectedCar.getTitle());
        carTitleTextView.setText(selectedCar.getTitle());


        RequestCreator albumImageRequest = Picasso.with(getActivity()).load(selectedCar.getMainImgURL());
        RequestCreator backgroundImageRequest = Picasso.with(getActivity()).load(selectedCar.getMainImgURL()).fit().centerCrop();

            albumImageRequest.noFade();
            backgroundImageRequest.noFade();
            backgroundImage.setAlpha(0f);
            getActivity().getWindow().getSharedElementEnterTransition().addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    backgroundImage.animate().setDuration(1000).alpha(1f);
                }
            });

        albumImageRequest.into(carImageView, mImageCallback);
        backgroundImageRequest.into(backgroundImage);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        launchIcon = (ImageView) view.findViewById(R.id.launchIcon);
        launchIcon.setVisibility(View.GONE);
        launchIcon.bringToFront();
        launchIcon.setOnClickListener(launchIconListener);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_downloading);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        detailedTitleTextView = (TextView) view.findViewById(R.id.detailedTitleTextView);
        carPriceTextView = (TextView) view.findViewById(R.id.carPriceTextView);
        pageDisplayTextView = (TextView) view.findViewById(R.id.pageDisplayTextView);
        carDescriptionTextView = (TextView) view.findViewById(R.id.carDescTextView);
        isFavoritedCheckBox = (CheckBox) view.findViewById(R.id.isFavoritedCheckBox);
        favoriteFAB = (FloatingActionButton) view.findViewById(R.id.favoriteFAB);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        specsTableLayout = (TableLayout) view.findViewById(R.id.specsTableLayout);

        dbTools = DBTools.getInstance(getContext());

        carID = getCarID();
        selectedCar = dbTools.getFavCar(carID);
        if(selectedCar == null) {
            for (Car car : Globals.getInstance().getCars()) {
                if (car.getCarID().equals(carID)) {
                    selectedCar = car;
                    break;
                }
            }
        }
        carPriceTextView.setText(getString(R.string.price_value_text_view, selectedCar.getPrice()));
        //carImageView = (ImageView) view.findViewById(R.id.carImageView);

        //Picasso.with(getActivity()).load(selectedCar.getMainImgURL()).fit().into(carImageView);

        if(isFavorited(selectedCar)) {
            isFavoritedCheckBox.setChecked(true);
        }
        isFavoritedCheckBox.setOnCheckedChangeListener(isFavoritedListener);

        new MyAsyncTask().execute(selectedCar);
    }

    private class MyAsyncTask extends AsyncTask<Car, Void, Car> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Car doInBackground(Car... car) {
            LinkedHashMap<String, String> specs;
            ArrayList<String> imgURLs;
            try {
                Car selectedCar = car[0];
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
                Element specKey = specOuter.select("dt").get(0);
                Element specValue;
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
                int numberOfImgs = outerClass.select("img.centered-image").last().attr("data-index").equals("") ?
                        0 : Integer.parseInt(outerClass.select("img.centered-image").last().attr("data-index"));
                while(counter <= numberOfImgs) {
                    imgURLs.add(outerClass.select("img.centered-image").get(counter).attr("data-src"));
                    counter++;
                }
                selectedCar.setName(detailedTitle);
                selectedCar.setImgURLs(imgURLs);
                selectedCar.setSpecs(specs);
                selectedCar.setDescription(carDescription);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return selectedCar;
        }

        @Override
        protected void onPostExecute(Car selectedCar) {
            setUpUI(selectedCar);
        }

        public void setUpUI(Car selectedCar) {

            //carTitleTextView.setText(selectedCar.getTitle());
            detailedTitleTextView.setText(selectedCar.getName());
            //carPriceTextView.setText(getString(R.string.price_value_text_view, selectedCar.getPrice()));
            carDescriptionTextView.setText(selectedCar.getDescription());

            for (Map.Entry<String,String> spec : selectedCar.getSpecs().entrySet())
                inflateScrollView(spec.getKey(), spec.getValue());

            progressBar.setVisibility(View.GONE);
            /* Handle image gallery */
            ImagePagerAdapter adapter = new ImagePagerAdapter(selectedCar);
            final int totalImages = adapter.getCount();
            pageDisplayTextView.setText(getString(R.string.viewpager_page_text_view, 1, totalImages));
            /*
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int currentState;
                int currentPosition = 0;
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    pageDisplayTextView.setText((getString(R.string.viewpager_page_text_view, position+1, totalImages)));
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
            */
        }
    }
    private class ImagePagerAdapter extends PagerAdapter {

        Car selectedCar;

        public ImagePagerAdapter(Car selectedCar) {
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
            return view ==  object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = getContext();
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Picasso.with(context).load(selectedCar.getImgURLs().get(position)).fit().into(imageView);
            (container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    public void inflateScrollView(String specKey, String specValue) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newSpecsRow = inflater.inflate(R.layout.info_specs_row, null);
        TextView specKeyTextView = (TextView) newSpecsRow.findViewById(R.id.specKeyTextView);
        TextView specValueTextView = (TextView) newSpecsRow.findViewById(R.id.specValueTextView);

        specKeyTextView.setText(specKey);
        specValueTextView.setText(specValue);

        specsTableLayout.addView(newSpecsRow);
    }

    public CompoundButton.OnCheckedChangeListener isFavoritedListener = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

            if (isChecked) {
                if(isFavorited(selectedCar))
                    return;
                Globals.getInstance().addFavoritedCar(selectedCar);
                dbTools.addFavCar(selectedCar);
            }
            else {
                dbTools.deleteFavCar(selectedCar);
                /*
                for(Car theCar: Globals.getInstance().getFavoritedCars()) {
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

    public View.OnClickListener launchIconListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Log.d("HMM", "HUEHEU");
                /* Launch a new Activity to show our DetailsFragment */
            Intent intent = new Intent(getActivity(), CarInfoActivity.class);

                        /* Define the class Activity to call */

                        /* Pass along the currently selected index assigned to the keyword index */
            intent.putExtra(CAR_ID, selectedCar.getCarID());

                        /* Call for the Activity to open */
            startActivity(intent);

        }
    };

    public boolean isFavorited(Car selectedCar) {
        return dbTools.checkForCar(selectedCar.getCarID());
    }

    private void startPostponedEnterTransition2() {
            carImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    carImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
    }
}