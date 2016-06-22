package com.timitoc.groupic.utils;

import com.timitoc.groupic.models.ImageItem;

/**
 * Created by timi on 22.06.2016.
 */
public class SaveLocalManager {

    public static ImageItem prepared;

    public static void prepare(ImageItem item) {
        prepared = item;
    }

    public static void savePrepared() {
        if (prepared == null)
            throw new RuntimeException("No ImageItem prepared");
        System.out.println("Proceeding to save item " + prepared.getId() + " " + prepared.getTitle());
    }
}
