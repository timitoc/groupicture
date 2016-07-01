package com.timitoc.groupic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.timitoc.groupic.models.ImageItem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

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
        ImageLoader loader = VolleySingleton.getInstance(null).getImageLoader();
        loader.get(prepared.getRequestUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() == null)
                    makeError();
                else {
                    saveBitmapOnLocal(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                makeError();
            }
        });
    }

    public static void deletePrepared() {
        if (prepared == null)
            throw new RuntimeException("No ImageItem prepared");
        System.out.println("Proceeding to delete item " + prepared.getId() + " " + prepared.getTitle());
        deleteBitmapOnLocal(constructImageFileName(prepared));
    }

    public static void saveBitmapOnLocal(Bitmap bitmap) {
        File localDirectory = new File(Global.phoneStoragePath);
        localDirectory.mkdirs();
        File imageFile = new File (localDirectory, constructImageFileName(prepared));
        if (imageFile.exists()) imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            PreferenceImageDataManager.saveFile(constructImageFileName(prepared));
            Toast.makeText(Global.baseActivity, "Successfully saved file into memory", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            makeError();
        }
    }

    /**
     *
     * @param imageFileName File name for the image to be erased
     * @return Returns true if the file existed and the deletion was successful.
     */
    private static boolean deleteBitmapOnLocal(String imageFileName) {
        File localDirectory = new File(Global.phoneStoragePath);
        localDirectory.mkdirs();
        File imageFile = new File(localDirectory, imageFileName);
        PreferenceImageDataManager.deleteFile(imageFileName);
        if (imageFile.exists())
            return imageFile.delete();
        return false;
    }

    public static Bitmap getBitmapFromLocal(ImageItem item) {
        String path = Global.phoneStoragePath + "/" + constructImageFileName(item);
        return BitmapFactory.decodeFile(path);
    }

    public static void makeError() {
        System.out.println("Couldn't save");

    }

    public static String constructImageFileName(ImageItem item) {
        return "GI#" + Integer.toHexString(item.getId());
    }


    public static boolean alreadySaved(ImageItem item) {
        return PreferenceImageDataManager.alreadySaved(constructImageFileName(item));
    }


    private static class PreferenceImageDataManager{

        private static SharedPreferences preferences;
        private static SharedPreferences.Editor editor;
        private static Set<String> filesSet;
        private static final String PREFERENCE_TAG = "local_images_set";

        private static void init() {
            System.out.println("init called");
            if (!PreferenceImageDataManager.isPreferenceDataInitialized())
                PreferenceImageDataManager.initializePreferenceData();
        }

        public static boolean isPreferenceDataInitialized() {
            return filesSet != null;
        }

        public static void initializePreferenceData() {
            System.out.println("init continued");
            preferences = PreferenceManager.getDefaultSharedPreferences(Global.baseActivity);

            filesSet = preferences.getStringSet(PREFERENCE_TAG, new HashSet<String>());
            System.out.println(filesSet.toString());
        }

        public static boolean alreadySaved(String fileName) {
            init();
            return filesSet.contains(fileName);
        }

        public static void saveFile(String fileName) {
            init();
            editor = preferences.edit();
            filesSet.add(fileName);
            editor.putStringSet(PREFERENCE_TAG, filesSet);
            boolean worked = editor.commit();
            if (!worked)
                System.out.println("Failed to delete file to preferences");
            else {
                System.out.println("Successfully save file to preferences");
                System.out.println(fileName);
                System.out.println(filesSet.toString());
                preferences = PreferenceManager.getDefaultSharedPreferences(Global.baseActivity);
                filesSet = preferences.getStringSet(PREFERENCE_TAG, null);
                System.out.println(filesSet.toString());
            }
        }

        public static void deleteFile(String fileName) {
            init();
            editor = preferences.edit();
            filesSet.remove(fileName);
            editor.putStringSet(PREFERENCE_TAG, filesSet);
            boolean worked = editor.commit();
            if (!worked)
                System.out.println("Failed to delete file from preferences");
            else
                System.out.println("Successfully deleted file from preferences");
        }
    }

}
