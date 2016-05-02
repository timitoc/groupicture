package com.timitoc.groupic.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by timi on 27.04.2016.
 */
public class Global {

    public static boolean want_login = false;
    public static boolean logging_out = false;
    public static int user_id = -1;
    public static int current_folder_id;
    public static int current_group_id;
    public static final String MY_PRIVATE_KEY = "e0ac083d489e17881e5ebad362c0048d";
    public static final String MY_PUBLIC_KEY = "6f21f9c06e3a78f3ca345bc126967076";
    public static final int PICK_IMAGE_REQUEST = 1;


    public static Runnable onAddMenuItemClicked = new Runnable() {
        @Override
        public void run() {
            System.out.println("Add menu item clicked");
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
