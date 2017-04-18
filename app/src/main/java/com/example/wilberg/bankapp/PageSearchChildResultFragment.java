package com.example.wilberg.bankapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wilberg.bankapp.Model.Car;
import com.example.wilberg.bankapp.Util.NetworkUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by WILBERG on 2/18/2017.
 */
public class PageSearchChildResultFragment extends Fragment implements AsyncTaskCallback, View.OnClickListener {

    // Callback interfaces.
    private OnRowClickedListener mListener;
    private OnConnectionErrorListener mConnectionListener;

    // NetworkFragment instance, handles the AsyncTask.
    private NetworkFragment mNetworkFragment;

    // Intent-related items
    private static final String EXTRA_CAR_ID = "com.example.wilberg.bankapp.EXTRA_CAR_ID";
    private static final String EXTRA_START_PAGE_POSITION = "com.example.wilberg.bankapp.EXTRA_START_PAGE_POSITION";
    private static final String EXTRA_CURRENT_PAGE_POSITION = "com.example.wilberg.bankapp.EXTRA_CURRENT_PAGE_POSITION";

    // Fragment arguments
    private static final String ARGUMENT_INPUT_VALUE = "ARGUMENT_INPUT_VALUE";
    private static final String ARGUMENT_SORT_METHOD = "ARGUMENT_SORT_METHOD";

    private String mNumberOfResults;
    //TODO: FIX UPDATE SCROLLVIEW WHEN AT BOTTOM.
    private int mCarOnUrlPage;
    private int mCarsPerDataPull;
    private int mPage;
    private int row;
    private int mScollYPosition;

    private List<Car> mCars = new ArrayList<>();
    private boolean mInListView;
    boolean mDualPane;
    private boolean mSwipeRefresh;

    /* Store data passed when fragment is reentered. */
    private Bundle mReenterState;

    /* RecyclerView */
    private RecyclerView mRvCars;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CarsAdapter mCarsAdapter;
    private android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    /* Views */
    private TextView mResultCount;
    private ImageView mListMenuToggle;
    private ProgressDialog mProgressDialog;
    private boolean mDownloading;

    public PageSearchChildResultFragment() {
        // Required empty public constructor.
    }

    /*Use newInstance to pass arguments to fragment*/
    public static PageSearchChildResultFragment newInstance(String inputValue, String sortMethod) {
        PageSearchChildResultFragment fragment = new PageSearchChildResultFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_INPUT_VALUE, inputValue);
        args.putString(ARGUMENT_SORT_METHOD, sortMethod);
        fragment.setArguments(args);
        return fragment;
    }

    public String getSortMethod() { return getArguments().getString(ARGUMENT_SORT_METHOD); }

    public interface OnRowClickedListener {
        void onRowClicked(String inputValue, ImageView carImage);
    }

    @Override
    public void handleProgressLoader() {
        /* handle mProgressDialog */
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(R.style.CustomProgressBar);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setIndeterminate(false);
        if(!mSwipeRefresh) {
            mProgressDialog.show();
        }
    }

    @Override
    public void updateFromAsyncTask(Document resultDocument) {
        if(resultDocument != null) {
            /* Total results found */
            mNumberOfResults = resultDocument.select("div.flex-grow1.rightify").select("b").first().ownText();

            int count = 0;
            while (count < mCarsPerDataPull) {

                Log.d("RUNNING1", "YO");
                Element outerClass = resultDocument.select("div.line.flex.align-items-stretch.wrap.cols1upto480.cols2upto990.cols3from990")
                        .select("div.unit.flex.align-items-stretch.result-item").get(count);
                if(outerClass != null) {
                    String carId = outerClass.select("a").first().attr("id");
                    String carImgURL = outerClass.select("img").first().attr("src");

                    Element carLocation = outerClass.select("span.licorice.valign-middle").get(0);
                    Element carTitle = outerClass.select("h3.t4.word-break.mhn.result-item-heading").get(0);

                    Element carInfoElement = outerClass.select("p.t5.word-break.mhn").get(0);
                    if( carInfoElement != null) {
                        Element carYear = carInfoElement.select("span.prm").get(0);
                        Element carDistance = carInfoElement.select("span.prm").get(1);
                        Element carPrice = carInfoElement.select("span.prm").get(2);

                                        /* Create new car object with pulled data*/
                        Car theCar = new Car(carTitle.ownText(), carId, carTitle.ownText(),
                                carYear.ownText(), carDistance.ownText(), carPrice.ownText(), null, null, null, null, null, carLocation.ownText(), carImgURL, null, null, null);
                        mCars.add(theCar);
                    }
                } count++;
            } Globals.getInstance().updateCars(mCars);
        }
        else {
            mConnectionListener.handleConnectionError();
        }
        setUpUI();
    }

    @Override
    public boolean getNetworkConnection() {
        return NetworkUtils.isConnected(getContext());
    }

    @Override
    public void onProgressUpdate(int progressCode, int progressStatus) {
        switch(progressCode) {
            // Add UI behavior for progress updates here, if needed
            case ConnectionStatus.ERROR:
                break;
            case ConnectionStatus.CONNECT_SUCCESS:
                break;
        }
    }

    @Override
    public void finishAsyncTask() {
        mDownloading = false;
        if(mNetworkFragment != null)
            mNetworkFragment.cancelMyAsyncTask();

    }

    //Force parent to implement interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setExitSharedElementCallback(mCallback);
        if(getParentFragment() instanceof OnRowClickedListener)  mListener = (OnRowClickedListener) getParentFragment();
        else
            throw new ClassCastException(context.toString() + " must implement OnRowClickedListener");

        if(getParentFragment() instanceof OnConnectionErrorListener)  mConnectionListener = (OnConnectionErrorListener) getParentFragment();
        else
            throw new ClassCastException(context.toString() + " must implement OnConnectionErrorListener");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCarsPerDataPull = 10;
        mInListView = true;
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), createURL());
        mNetworkFragment.setFragment(this);
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

        mResultCount = (TextView) view.findViewById(R.id.resultCountTextView);
        mRvCars = (RecyclerView) view.findViewById(R.id.rvCars);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        // List/Grid toggle
        mListMenuToggle = (ImageView) view.findViewById(R.id.listMenuToggle);
        mListMenuToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_apps_black_24dp));
        mListMenuToggle.setOnClickListener(this);

        mPage = Globals.getInstance().getPage();

        // Set up SwipeRefresh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh = true;
                swipeRefreshLayout.setRefreshing(true);
                clearCars();
                startAsyncTask();
            }
        });
        // Animate recyclerView to appear from below the screen.
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mRvCars.setTranslationY(displayMetrics.heightPixels);

        //carScrollView = (NestedScrollView) view.findViewById(R.id.carScrollView);
        //carTableScrollView = (TableLayout) view.findViewById(R.id.carTableScrollView);

        //scroll listener added for sdk 23+
        /*
        if (Build.VERSION.SDK_INT >= 23)
            carScrollView.setOnScrollChangeListener(onScrollListener);

        //scroll listener for sdk 22 and lower
        else {
        */
        /* Handle swipeRefreshLayout */
        //final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.carScrollView);
        /*
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
                        // if(diff < 720)
                            //prepareInflation();
                    }
                };

        // When reached bottom, inflate more rows
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
        */
        // }
        startAsyncTask();
    }

    @Override
    public void onClick(View view) {
        // Handle toggle between list and grid.
        if(mInListView) {
            mInListView = false;
            mListMenuToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_reorder_black_24dp));
            int position = (int) mRvCars.getY();
            mRvCars.getVerticalScrollbarPosition();
            mStaggeredGridLayoutManager.setSpanCount(2);
           // mStaggeredGridLayoutManager.scrollToPosition(mScollYPosition);
            mRvCars.setLayoutManager(mStaggeredGridLayoutManager);

            mCarsAdapter = new CarsAdapter(PageSearchChildResultFragment.this.getActivity(), mCars);
            mCarsAdapter.setInListView(mInListView);
            mRvCars.setAdapter(mCarsAdapter);
            Log.d("position", Integer.toString(mScollYPosition));
            mRvCars.scrollToPosition(5);
            //mCarsAdapter.notifyItemInserted(0);
            //mRvCars.setLayoutManager(mStaggeredGridLayoutManager);
        }
        else {
            mInListView = true;
            mListMenuToggle.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_apps_black_24dp));
            int position = (int) mRvCars.getY();
            mStaggeredGridLayoutManager.setSpanCount(1);


            mCarsAdapter = new CarsAdapter(PageSearchChildResultFragment.this.getActivity(), mCars);
            mCarsAdapter.setInListView(mInListView);
            mRvCars.setAdapter(mCarsAdapter);
            mRvCars.setLayoutManager(mStaggeredGridLayoutManager);
            Log.d("position", Integer.toString(mScollYPosition));
            mRvCars.scrollTo(0, mScollYPosition);
            //mRvCars.setLayoutManager(mStaggeredGridLayoutManager);
        }
        //adapter.setInListView(listView);
        //mRvCars.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

    }

    public void startAsyncTask() {
        if(!mDownloading && mNetworkFragment != null) {
            mNetworkFragment.startMyAsyncTask();
            mDownloading = true;
        }

        /*          AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.no_internet_access);
                    alertDialog.show();
         */
    }
/*
    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        boolean showProgressBar;
        public MyAsyncTask(Boolean showProgressBar) {
            super();
            this.showProgressBar = showProgressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


    */

    public String createURL() { return getResources().getString(R.string.url_search, getSortMethod(), mPage); }

    public void setUpUI() {

        // Display number of results.
        mResultCount.setText(getString(R.string.search_result_text_view, mNumberOfResults));

        // Set up Car adapter
        mCarsAdapter = new CarsAdapter(PageSearchChildResultFragment.this.getActivity(), mCars);
        mCarsAdapter.setOnItemClickListener(new CarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, ImageView carImage, TextView carInfoTextView) {

                CarInfoFragment carInfoFragment = (CarInfoFragment)getFragmentManager().findFragmentById(R.id.infoFragmentContainer);
                String carID = mCars.get(position).getCarID();
                /* If different row is clicked */
                if(carInfoFragment == null || !carInfoFragment.getCarID().equals(carID)) {
                    /* If in landscape mode */
                    if (mDualPane)
                        mListener.onRowClicked(carID, carImage);
                    else {
                        Intent carIntent = new Intent(getActivity(), CarInfoActivity.class);
                        carIntent.putExtra(EXTRA_CAR_ID, mCars.get(position).getCarID());
                        carIntent.putExtra(EXTRA_START_PAGE_POSITION, position);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Pair<View, String> p1 = Pair.create((View) carImage, carImage.getTransitionName());
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1);
                            startActivity(carIntent, optionsCompat.toBundle());
                        }
                        else {
                            //TODO: pre LOLLIPOP
                            startActivity(carIntent);
                        }
                    }
                }
            }
        });
        mRvCars.setAdapter(mCarsAdapter);
        //mRvCars.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvCars.setLayoutManager(mStaggeredGridLayoutManager);
        //mRvCars.addItemDecoration(new DividerItemDecoration(getContext()));
        mRvCars.addItemDecoration(new DividerItemDecoration(5));
        mRvCars.animate().translationY(0);
        if(Build.VERSION.SDK_INT >= 23) {
            /*
            mRvCars.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    int scrollY = mRvCars.getScrollY();
                    mScollYPosition += i1;
                    Log.d("SCROLLY", Integer.toString(i));
                    Log.d("SCROLLY", Integer.toString(i1));
                    Log.d("SCROLLY", Integer.toString(i2));
                    Log.d("SCROLLY", Integer.toString(i3));
                    if(scrollY == 0)
                        swipeRefreshLayout.setEnabled(true);
                    else
                        swipeRefreshLayout.setEnabled(false);
                }
            });
            */
            mRvCars.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mScollYPosition += dy;
                    Log.d("SCROLLY", Integer.toString(dy));
                }
            });
        }

        //prepareInflation();
        //TODO: HANDLE dual pane.
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if(mReenterState != null) {
                int previousPosition = mReenterState.getInt(EXTRA_START_PAGE_POSITION);
                int currentPosition = mReenterState.getInt(EXTRA_CURRENT_PAGE_POSITION);
                if(previousPosition != currentPosition) {
                    //User swiped to a new page
                    String transitionName = "carImage" + mCars.get(currentPosition).getCarID();
                    View currentSharedElement = mRvCars.findViewWithTag(transitionName);
                    if(currentSharedElement != null) {
                        names.clear();
                        sharedElements.clear();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            names.add(currentSharedElement.getTransitionName());
                            sharedElements.put(currentSharedElement.getTransitionName(), currentSharedElement);
                        }
                        else {
                            //TODO: Pre LOLLIPOP
                        }
                    }
                }
                mReenterState = null;
            }
        }
    };

    public void prepareInflation() {
        if(mCarOnUrlPage == 50) {
            mCarOnUrlPage = 0;
            mPage++;
            String url = createURL();
            mNetworkFragment.updateURL(url);
            startAsyncTask();
        }
        else {
            int count = 0;
            for (int i = row; i < Globals.getInstance().getCars().size() && count < 9; i++) {
                row++;
                count++;
                mCarOnUrlPage++;
            }
        }
    }

    public void clearCars() {
        mCars.clear();
        mRvCars.removeAllViews();
    }

    public void handleActivityReenter(Intent data) {
        mReenterState = new Bundle(data.getExtras());
        int previousPosition = mReenterState.getInt(EXTRA_START_PAGE_POSITION);
        int currentPosition = mReenterState.getInt(EXTRA_CURRENT_PAGE_POSITION);
        if(previousPosition != currentPosition)
            mRvCars.scrollToPosition(currentPosition);
        postponeEnterTransition();
        handlePostponedEnterTransition();
    }

    private void handlePostponedEnterTransition() {
        mRvCars.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRvCars.getViewTreeObserver().removeOnPreDrawListener(this);
                mRvCars.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

}