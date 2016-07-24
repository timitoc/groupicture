package com.timitoc.groupic.utils;

import android.view.MenuItem;
import com.timitoc.groupic.R;

/**
 * Created by timi on 18.07.2016.
 */
public class ConnectionStateManager {

    public enum UsingState {
        ONLINE, OFFLINE, UNDEFINED
    }
    private static UsingState usingState = UsingState.UNDEFINED;
    private static MenuItem controller;


    public static UsingState getUsingState() {
        return usingState;
    }

    public static void setController(MenuItem menuItem) {
        controller = menuItem;
        calibrateControllerState();
    }

    public static void setUsingState(UsingState usingState) {
        ConnectionStateManager.usingState = usingState;
        calibrateControllerState();
    }

    public static void calibrateControllerState() {
        if (controller == null)
            return;
        switch (usingState) {
            case ONLINE:
                controller.setTitle("Online");
                controller.setIcon(R.drawable.online);
                break;
            case OFFLINE:
                controller.setTitle("Offline");
                controller.setIcon(R.drawable.offline);
                break;
            case UNDEFINED:
                controller.setTitle("Weak connection");
                controller.setIcon(R.drawable.weakcon);
                break;
        }
    }

    public static void decreaseUsingState() {
        if (usingState == UsingState.ONLINE)
            setUsingState(UsingState.UNDEFINED);
    }

    public static void increaseUsingState() {
        if (usingState == UsingState.UNDEFINED)
            setUsingState(UsingState.ONLINE);
    }
}
