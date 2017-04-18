package com.example.wilberg.bankapp;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.wilberg.bankapp.DB.DBTools;
import com.example.wilberg.bankapp.Model.Car;

import com.example.wilberg.bankapp.Util.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CarInfoFragment extends Fragment implements AsyncTaskCallback, View.OnClickListener {

    // Intent-related items
    private final static String EXTRA_CAR_ID = "com.example.wilberg.bankapp.EXTRA_CAR_ID";

    // Fragment arguments
    private final static String ARGUMENT_CAR_ID = "ARGUMENT_CAR_ID";

    // Callback to handle image transition.
    private final Callback mImageCallback = new Callback() {
        @Override
        public void onSuccess() {
            handlePostponedEnterTransition();
        }

        @Override
        public void onError() {
            handlePostponedEnterTransition();
        }
    };

    private NetworkFragment mNetworkFragment;
    private OnConnectionErrorListener mConnectionListener;

    private DBTools dbTools;
    private Car mCurrentCar;
    private String mCarID;
    private boolean mDownloading;
    private boolean mDuelPane;
    private boolean mIsChecked;
    private Drawable mCheckboxSrc;

    private TextView mCarDescriptionTextView;
    private TextView mDetailedTitleTextView;
    private TextView mPageDisplayTextView;
    private CheckBox checkBoxBorder;
    private CheckBox mFavoriteCheckBox;
    private ImageView mCarImageView;
    private ImageView mLaunchIcon;
    private ViewPager mCarGalleryViewPager;
    private RelativeLayout mMainRelativeLayout;
    private TableLayout mSpecsTableLayout;

    /*Use newInstance to pass arguments to fragment*/
    public static CarInfoFragment newInstance(FragmentManager fragmentManager, int position) {

        CarInfoFragment infoFragment = new CarInfoFragment();
        Bundle args = new Bundle();
        String carID = Globals.getInstance().getCars().get(position).getCarID();
        args.putString(ARGUMENT_CAR_ID, carID);
        infoFragment.setArguments(args);
        return infoFragment;


        /*
        CarInfoFragment fragment = (CarInfoFragment) fragmentManager.findFragmentByTag(TAG);
        if(fragment == null) {
            fragment = new CarInfoFragment();
            Bundle args = new Bundle();
            String carID = Globals.getInstance().getCars().get(position).getCarID();
            args.putString(ARGUMENT_CAR_ID, carID);
            fragment.setArguments(args);
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commit();
                    */

    }

    public String getCarID() {
        return getArguments().get(ARGUMENT_CAR_ID).toString();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getActivity() instanceof OnConnectionErrorListener) mConnectionListener = (OnConnectionErrorListener) getActivity();
        else
            throw new ClassCastException(context.toString() + " must implement OnConnectionErrorListener");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Get DBHelper, to handle favorite cars.
        dbTools = DBTools.getInstance(getContext());

        //get selected car object.
        mCurrentCar = getCar();
        // Instantiate network fragment to handle AsyncTask.
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), createURL());
        mNetworkFragment.setFragment(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check is in dual pane.
        View infoFragmentContainer = getActivity().findViewById(R.id.infoFragmentContainer);
        mDuelPane = (infoFragmentContainer != null) && (infoFragmentContainer.getVisibility() == View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Views
        ((TextView) view.findViewById(R.id.carTitleTextView)).setText(mCurrentCar.getTitle());
        ((TextView) view.findViewById(R.id.carPriceTextView)).setText(getString(R.string.price_value_text_view, mCurrentCar.getPrice()));
        mCarDescriptionTextView = (TextView) view.findViewById(R.id.carDescTextView);
        mDetailedTitleTextView = (TextView) view.findViewById(R.id.detailedTitleTextView);
        mPageDisplayTextView = (TextView) view.findViewById(R.id.pageDisplayTextView);
        mFavoriteCheckBox = (CheckBox) view.findViewById(R.id.favoriteCheckBox);
        checkBoxBorder = (CheckBox) view.findViewById(R.id.checkBoxBorder);
        mCarImageView = (ImageView) view.findViewById(R.id.carImageView);
        mCarGalleryViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mMainRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
        mSpecsTableLayout = (TableLayout) view.findViewById(R.id.specsTableLayout);


        //toolbarFAB = (Toolbar) view.findViewById(R.id.toolbarFAB);

        if(mDuelPane) {
            mLaunchIcon = (ImageView) view.findViewById(R.id.launchIcon);
            mLaunchIcon.setVisibility(View.VISIBLE);
            mLaunchIcon.bringToFront();
            mLaunchIcon.setOnClickListener(this);
        }

        mCheckboxSrc = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_border_black_48dp);
        mCheckboxSrc.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        checkBoxBorder.setButtonDrawable(mCheckboxSrc);
        checkBoxBorder.bringToFront();
        if(isFavorited(mCurrentCar)) {
            mIsChecked = true;
            mCheckboxSrc = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_black_48dp);
            mCheckboxSrc.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            mFavoriteCheckBox.setButtonDrawable(mCheckboxSrc);
        }
        else {
            mCheckboxSrc = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_black_48dp);
            mCheckboxSrc.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), PorterDuff.Mode.SRC_IN);
            mFavoriteCheckBox.setButtonDrawable(mCheckboxSrc);
        }

        // Handle image transition.
        RequestCreator albumImageRequest = Picasso.with(getActivity()).load(mCurrentCar.getMainImgURL());
        albumImageRequest
                .noFade()
                .into(mCarImageView, mImageCallback);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCarImageView.setTransitionName("carImage" + mCarID);
            getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) { }

                @Override
                public void onTransitionEnd(Transition transition) {
                    //mFavoriteFAB.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTransitionCancel(Transition transition) { }

                @Override
                public void onTransitionPause(Transition transition) { }

                @Override
                public void onTransitionResume(Transition transition) { }
            });
        }

        // Handle FAB.
        mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    mCheckboxSrc = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_black_48dp);

                    mCheckboxSrc.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
                    mFavoriteCheckBox.setButtonDrawable(mCheckboxSrc);
                }
                else {
                    mCheckboxSrc = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_black_48dp);
                    mCheckboxSrc.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), PorterDuff.Mode.SRC_IN);
                    mFavoriteCheckBox.setButtonDrawable(mCheckboxSrc);;
                }
            }
        });


        /* FAB ICON
        Drawable mFabSrc = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_black_24dp);
        mFabSrc.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey_dark), PorterDuff.Mode.SRC_IN);
                    mFavoriteFAB.setImageDrawable(mFabSrc);


        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareFAB.hide();
                showToolbarFAB();
            }
        });
        toolbarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideToolbarFAB();
            }
        });
        */

        /* Start asyncTask if network available */

        startAsyncTask();
    }

    private void startAsyncTask() {
        if(!mDownloading && mNetworkFragment != null) {
            mDownloading = true;
            mNetworkFragment.startMyAsyncTask();
        }
    }

    @Override
    public void handleProgressLoader() {

    }

    @Override
    public void updateFromAsyncTask(Document resultDocument) {

        if(resultDocument != null) {
            Log.d("DOCNOTNULL", "Yo");
            LinkedHashMap<String, String> specs;
            ArrayList<String> imgURLs;

            //various info on item
            String detailedTitle = resultDocument.select("p.tcon.mtn").first().ownText();
            String carDescription = "No description available";
            Element descElement = resultDocument.select("div.mbl.object-description").select("p").first();
            if (descElement != null) carDescription = descElement.ownText();

            //specs
            specs = new LinkedHashMap<>();
            Element specOuter = resultDocument.select("dl.r-prl.mhn.multicol.col-count1upto640.col-count2upto768.col-count1upto990.col-count2from990").get(1);
            Element specKeyLast = specOuter.select("dt").last();
            Element specKey = specOuter.select("dt").get(0);
            Element specValue;
            int specTableRow = 0;
            while (!specKeyLast.equals(specKey)) {

                specKey = specOuter.select("dt").get(specTableRow);
                specValue = specOuter.select("dd").get(specTableRow);
                specs.put(specKey.ownText(), specValue.ownText());
                specTableRow++;
            }

            //images
            imgURLs = new ArrayList<>();
            Element outerClass = resultDocument.select("div.line").first();
            //first image
            imgURLs.add(outerClass.select("img").get(0).attr("src"));

            int counter = 1;
            int numberOfImgs = outerClass.select("img.centered-image").last().attr("data-index").equals("") ?
                    0 : Integer.parseInt(outerClass.select("img.centered-image").last().attr("data-index"));
            while (counter <= numberOfImgs) {
                imgURLs.add(outerClass.select("img.centered-image").get(counter).attr("data-src"));
                counter++;
            }
            mCurrentCar.setName(detailedTitle);
            mCurrentCar.setImgURLs(imgURLs);
            mCurrentCar.setSpecs(specs);
            mCurrentCar.setDescription(carDescription);

            // Add data to views after Asynctask is completed.
            setUpUI(mCurrentCar);
        }
        else {
            mConnectionListener.handleConnectionError();
        }

    }

    @Override
    public boolean getNetworkConnection() {
        return NetworkUtils.isConnected(getContext());
    }

    @Override
    public void onProgressUpdate(int progressCode, int progressStatus) {

    }

    @Override
    public void finishAsyncTask() {
        if(mNetworkFragment != null) mNetworkFragment.cancelMyAsyncTask();
    }

    public void setUpUI(Car selectedCar) {

        Log.d("SETUPUI", selectedCar.getName());
        mDetailedTitleTextView.setText(selectedCar.getName());
        mCarDescriptionTextView.setText(selectedCar.getDescription());

        //inflate car specs to table
        for (Map.Entry<String,String> spec : selectedCar.getSpecs().entrySet())
            inflateScrollView(spec.getKey(), spec.getValue());
        animateMainView();
            /* Handle image gallery */
        ImagePagerAdapter adapter = new ImagePagerAdapter(selectedCar);
        final int totalImages = adapter.getCount();
        mPageDisplayTextView.setText(getString(R.string.viewpager_page_text_view, 1, totalImages));
            /*
            mCarGalleryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int currentState;
                int currentPosition = 0;
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mPageDisplayTextView.setText((getString(R.string.viewpager_page_text_view, position+1, totalImages)));
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
                    final int lastPosition = mCarGalleryViewPager.getAdapter().getCount() - 1;
                    if(currentPosition == 0) {
                        mCarGalleryViewPager.setCurrentItem(lastPosition, false);
                    } else if(currentPosition == lastPosition) {
                        mCarGalleryViewPager.setCurrentItem(0, false);
                    }
                }
            });
            mCarGalleryViewPager.setAdapter(adapter);
            mCarGalleryViewPager.setOffscreenPageLimit(mCurrentCar.getImgURLs().size());
            */
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.launchIcon:
                /* Launch a new Activity to show our InfoFragment in full size*/
                Intent intent = new Intent(getActivity(), CarInfoActivity.class);

            /* Pass along the currently selected carID assigned to the keyword CAR_ID */
                intent.putExtra(EXTRA_CAR_ID, mCurrentCar.getCarID());

            /* Call for the Activity to open */
                startActivity(intent);
                break;

        }

    }

    /*

    private class MyAsyncTask extends AsyncTask<Car, Void, Car> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Car doInBackground(Car... cars) {
            Car selectedCar = cars[0];
            String url = "https://m.finn.no/car/used/ad.html?finnkode=" + selectedCar.getCarID();
            String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21";
            int timeOut = 10000;

            LinkedHashMap<String, String> specs;
            ArrayList<String> imgURLs;

            Document doc = null;
            //Try to connect to URL
            try {
                Connection.Response response = Jsoup.connect(url).userAgent(userAgent)
                        .timeout(timeOut)
                        .ignoreHttpErrors(true).execute();

                int statusCode = response.statusCode();
                if(statusCode == 200)
                    doc = Jsoup.connect(url).get();
                else
                    System.out.println("received error code : " + statusCode);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(doc != null) {

                    //various info on item
                    String detailedTitle = doc.select("p.tcon.mtn").first().ownText();
                    String carDescription = "No description available";
                    Element descElement = doc.select("div.mbl.object-description").select("p").first();
                    if(descElement != null)
                        carDescription = descElement.ownText();

                    //specs
                    specs = new LinkedHashMap<>();
                    Element specOuter = doc.select("dl.r-prl.mhn.multicol.col-count1upto640.col-count2upto768.col-count1upto990.col-count2from990").get(1);
                    Element specKeyLast = specOuter.select("dt").last();
                    Element specKey = specOuter.select("dt").get(0);
                    Element specValue;
                    int specTableRow = 0;
                    while (!specKeyLast.equals(specKey)) {

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
                    while (counter <= numberOfImgs) {
                        imgURLs.add(outerClass.select("img.centered-image").get(counter).attr("data-src"));
                        counter++;
                    }
                    selectedCar.setName(detailedTitle);
                    selectedCar.setImgURLs(imgURLs);
                    selectedCar.setSpecs(specs);
                    selectedCar.setDescription(carDescription);
                }
            return mCurrentCar;
        }

        @Override
        protected void onPostExecute(Car selectedCar) {
            setUpUI(selectedCar);
        }

    }

    */
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

        mSpecsTableLayout.addView(newSpecsRow);
    }

    public View.OnClickListener launchIconListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            /* Launch a new Activity to show our InfoFragment in full size*/
            Intent intent = new Intent(getActivity(), CarInfoActivity.class);

            /* Pass along the currently selected carID assigned to the keyword CAR_ID */
            intent.putExtra(EXTRA_CAR_ID, mCurrentCar.getCarID());

            /* Call for the Activity to open */
            startActivity(intent);
        }
    };

    /*
    public void showToolbarFAB() {

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;

        int cx = (int) ((screenWidth)  -  (mFavoriteFAB.getWidth() + mFavoriteFAB.getWidth() +  (getResources().getDimension(R.dimen.keyline_1))));
        int cy = (int)(getResources().getDimension(R.dimen.large_toolbar_size)) / 2;
        int finalRadius = Math.max(toolbarFAB.getWidth(), toolbarFAB.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(toolbarFAB, cx, cy, 0, finalRadius);
        toolbarFAB.setVisibility(View.VISIBLE);
        animator.start();
    }

    public void hideToolbarFAB() {

        int cx = toolbarFAB.getWidth() - ((mFavoriteFAB.getWidth()/2) + (int) (getResources().getDimension(R.dimen.keyline_1) /  getResources().getDisplayMetrics().density));
        int cy = toolbarFAB.getHeight() / 2;
        int initialRadius = Math.max(toolbarFAB.getWidth(), toolbarFAB.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(toolbarFAB, cx, cy, initialRadius, 0);
        animator.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                toolbarFAB.setVisibility(View.INVISIBLE);
            }
        });
        animator.start();
        shareFAB.show();
    }
    */

    public Car getCar() {

        mCarID = getCarID();
        Car mCar = dbTools.getFavCar(mCarID);
        if(mCar == null) {
            for (Car car : Globals.getInstance().getCars()) {
                if (car.getCarID().equals(mCarID)) {
                    mCar = car;
                    break;
                }
            }
        }
        return mCar;
    }

    public String createURL() {
        return getResources().getString(R.string.url_car_info, mCurrentCar.getCarID());
    }

    //return shared element to be transitioned, return null if view not visible
    public ImageView getCarImage() {
        if (isViewInBounds(getActivity().getWindow().getDecorView(), mCarImageView))
            return mCarImageView;
        return null;
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    private static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }

    public boolean isFavorited(Car car) {
        return dbTools.checkForCar(car.getCarID());
    }

    public void animateMainView() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int finalRadius = Math.max(screenWidth, screenHeight);
            Animator animator = ViewAnimationUtils.createCircularReveal(mMainRelativeLayout, 0, mCarImageView.getHeight(), 0, finalRadius);
            mMainRelativeLayout.setVisibility(View.VISIBLE);
            animator.start();
        }
        else {
            //TODO: pre LOLLIPOP
        }

    }

    private void handlePostponedEnterTransition() {
            mCarImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mCarImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
    }
}