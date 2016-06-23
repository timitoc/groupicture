package com.timitoc.groupic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;

/**
 * Created by timi on 27.04.2016.
 */
public class Global {

    public static boolean want_login = false;
    public static boolean logging_out = false;
    public static boolean confirm_save_image_on_local = true;
    public static int user_id = -1;
    public static int current_folder_id;
    public static int current_group_id;
    public static final String MY_PRIVATE_KEY = "e0ac083d489e17881e5ebad362c0048d";
    public static final String MY_PUBLIC_KEY = "6f21f9c06e3a78f3ca345bc126967076";
    public static final int PICK_IMAGE_REQUEST = 1;

    public static String phoneStoragePath = null;
    public static Activity baseActivity;

    public static void initializeSettings(Activity baseActivity) {
        phoneStoragePath = Environment.getExternalStorageDirectory().toString() + "/groupicture_images";
        SharedPreferences sharedPreferences = baseActivity.getPreferences(Context.MODE_PRIVATE);
        confirm_save_image_on_local = sharedPreferences.getBoolean("confirm_save_image_on_local", true);
        Global.baseActivity = baseActivity;
    }

    public static Runnable onAddMenuItemClicked = new Runnable() {
        @Override
        public void run() {
            System.out.println("Add menu item clicked");
        }
    };
    public static Runnable onRefreshMenuItemClicked = new Runnable() {
        @Override
        public void run() {
            System.out.println("Refresh menu item clicked");
        }
    };
    public static Runnable addImage = new Runnable() {
        @Override
        public void run() {
            System.out.println("nothing ever happens, and I wonder");
        }
    };

    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeBytes(imageBytes);
    }
}
