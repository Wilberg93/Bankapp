package com.example.wilberg.bankapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by WILBERG on 3/12/2017.
 */
public class NetworkFragment extends Fragment {

    // Fragment arguments
    public static final String ARGUMENT_URL = "ARGUMENT_URL";

    // TAGS
    public static final String TAG_NETWORK_FRAGMENT = "TAG_NETWORK_FRAGMENT";

    private AsyncTaskCallback mCallback;
    private MyAsyncTask mMyAsyncTask;
    private Fragment mCurrentFragment;
    private String mUrl;

    public static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {

        NetworkFragment fragment = (NetworkFragment) fragmentManager.findFragmentByTag(TAG_NETWORK_FRAGMENT);
        if(fragment == null) {
            fragment = new NetworkFragment();
            Bundle args = new Bundle();
            args.putString(ARGUMENT_URL, url);
            fragment.setArguments(args);
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG_NETWORK_FRAGMENT)
                    .commit();
        }
        else if(fragment.getArguments().getString(ARGUMENT_URL) != null)
            if(!fragment.getArguments().getString(ARGUMENT_URL).equals(url)) {
                fragment = new NetworkFragment();
                Bundle args = new Bundle();
                args.putString(ARGUMENT_URL, url);
                fragment.setArguments(args);
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG_NETWORK_FRAGMENT)
                        .commit();
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("ONATTACTH", "*****************************");
        if(mCurrentFragment instanceof AsyncTaskCallback) {
            mCallback = (AsyncTaskCallback) mCurrentFragment;
        }
        else
            throw new ClassCastException(mCurrentFragment + " must implement AsyncTaskCallback");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mUrl = getArguments().getString(ARGUMENT_URL);
        Log.d("MYURL", mUrl);


    }

    @Override
    public void onDestroy() {
        cancelMyAsyncTask();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DETACH", "NOGOOD");
        mCallback = null;
    }

    public void startMyAsyncTask() {
        cancelMyAsyncTask();
        mMyAsyncTask = new MyAsyncTask();
        Log.d("MYURL2", getArguments().getString(ARGUMENT_URL));
        mMyAsyncTask.execute(getArguments().getString(ARGUMENT_URL));
    }

    public void updateURL(String url) {
        mUrl = url;
    }

    public void cancelMyAsyncTask() {
        if(mMyAsyncTask != null) {
            mMyAsyncTask.cancel(true);
            mMyAsyncTask = null;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, MyAsyncTask.Result> {

        /* Wrapper class, so exceptions can be passed to the UI thread if occurred while running doInBackground() */
        class Result {
            public Document mResultDocument;
            public Exception mException;
            public Result(Document resultDoc) {
                mResultDocument = resultDoc;
            }
            public Result(Exception exception) {
                mException = exception;
            }
        }

        /* Cancel asyncTask if not connected to network*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mCallback != null) {
                if (!mCallback.getNetworkConnection()) {
                    /* Update with null data if no connection. Cancel asyncTask */
                    mCallback.updateFromAsyncTask(null);
                    cancel(true);
                }
                mCallback.handleProgressLoader();
            }
        }
        /* Background work */
        @Override
        protected Result doInBackground(String... urls) {
            Result result = null;
            if (!isCancelled() && urls != null && urls.length > 0) {
                String urlString = urls[0];

                try {
                    Log.d("RUNNING2", urlString);
                    Document resultDoc = downLoadUrl(urlString);
                    if (resultDoc != null) {
                        Log.d("RUNNING2.1", "YO");
                        result = new Result(resultDoc);
                    }
                    else
                        throw new IOException("No response");
                } catch (Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values.length >= 2)
                mCallback.onProgressUpdate(values[0], values[1]);
        }

        @Override
        protected void onPostExecute(Result result) {
            if(result != null && mCallback != null) {
                Log.d("RUNNING3", "YO");
                if(result.mException != null) {
                    /* TODO: Handle error */
                    Log.d("RUNNING NO", "YO");
                } else if(result.mResultDocument != null)
                    Log.d("RUNNING4", "YO");
                    mCallback.updateFromAsyncTask(result.mResultDocument);
                mCallback.finishAsyncTask();
            }
        }

        @Override
        protected void onCancelled(Result result) {
            /* TODO: Handle Cancelled AsyncTask */
        }

        public Document downLoadUrl(String url) {
            String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21";
            int timeOut = 10000;

            Document resultDoc = null;
            //Try to connect to URL
            try {
                Connection.Response response = Jsoup.connect(url).userAgent(userAgent)
                        .timeout(timeOut)
                        .ignoreHttpErrors(true).execute();
                publishProgress(AsyncTaskCallback.ConnectionStatus.CONNECT_SUCCESS);
                int statusCode = response.statusCode();
                if(statusCode == HttpURLConnection.HTTP_OK) resultDoc = Jsoup.connect(url).get();
                else
                    System.out.println("Received error code : " + statusCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultDoc;
        }
    }
    public void setFragment(Fragment fragment) {
        mCurrentFragment = fragment;
    }
}
