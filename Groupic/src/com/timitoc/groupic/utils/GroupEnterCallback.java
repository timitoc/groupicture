package com.timitoc.groupic.utils;

import android.support.annotation.Nullable;

/**
 * Created by timi on 10.07.2016.
 */
public interface GroupEnterCallback {

    /**
     * @param hasPassword Indicates if password is required for entering this group
     * @param input String for password. Can be null if hasPassword is false
     */
    void call(boolean hasPassword, @Nullable String input);

}
