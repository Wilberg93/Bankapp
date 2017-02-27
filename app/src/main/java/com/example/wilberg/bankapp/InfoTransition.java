package com.example.wilberg.bankapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by WILBERG on 2/25/2017.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class InfoTransition extends TransitionSet {
    public InfoTransition() {
        setOrdering(ORDERING_TOGETHER);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
        }
    }
}