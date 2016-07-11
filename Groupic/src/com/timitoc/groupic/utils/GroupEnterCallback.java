package com.timitoc.groupic.utils;

import android.support.annotation.Nullable;
import com.timitoc.groupic.models.GroupItem;

/**
 * Created by timi on 10.07.2016.
 */
public interface GroupEnterCallback {

    /**
     * @param groupItem Indicates the Group the user wants to enter
     * @param input String for password. Can be null if hasPassword is false
     */
    void call(GroupItem groupItem, @Nullable String input);

}
