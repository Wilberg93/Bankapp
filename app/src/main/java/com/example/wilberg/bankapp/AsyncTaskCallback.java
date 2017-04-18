package com.example.wilberg.bankapp;

import org.jsoup.nodes.Document;

/**
 * Created by WILBERG on 3/12/2017.
 */

/* Every class that wants to do an AsyncTask has to implement this interface */
public interface AsyncTaskCallback {
    interface ConnectionStatus {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
    }

    void handleProgressLoader();

    void updateFromAsyncTask(Document resultDocument);

    boolean getNetworkConnection();

    void onProgressUpdate(int progressCode, int progressStatus);

    void finishAsyncTask();

}
