package com.timitoc.groupic.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.timitoc.groupic.models.ImageItem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by timi on 22.06.2016.
 */
public class SaveLocalManager {

    private static ImageItem prepared;
    private static final String PREFERENCE_TAG_IMAGES = "local_images_set";
    private static final String PREFERENCE_TAG_FOLDERS = "local_folders_set";
    private static final String PREFERENCE_TAG_GROUPS = "local_groups_set";

    public static void prepare(ImageItem item) {
        prepared = item;
    }

    public static void savePrepared() {
        if (prepared == null)
            throw new RuntimeException("No ImageItem prepared");
        Toast.makeText(Global.baseActivity, "Saving...", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(Global.baseActivity, "Deleting...", Toast.LENGTH_SHORT).show();
        System.out.println("Proceeding to delete item " + prepared.getId() + " " + prepared.getTitle());
        deleteBitmapOnLocal(constructImageFileName(prepared));
        PreferenceImageDataManager.tryToDeleteFolder(prepared.getParentFolder().getId());
        PreferenceImageDataManager.tryToDeleteGroup(prepared.getParentFolder().getParentGroup().getId());
    }

    private static void saveBitmapOnLocal(Bitmap bitmap) {
        File localDirectory = new File(Global.phoneStoragePath);
        localDirectory.mkdirs();
        File imageFile = new File (localDirectory, constructImageFileName(prepared));
        if (imageFile.exists()) imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            PreferenceImageDataManager.saveFile(constructImageFileName(prepared), constructFilePrefName(prepared), constructGroupPrefName(prepared));
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
        if (imageFile.exists()) {
            Toast.makeText(Global.baseActivity, "Successfully removed file from memory", Toast.LENGTH_SHORT).show();
            return imageFile.delete();
        }
        return false;
    }

    public static Bitmap getBitmapFromLocal(ImageItem item) {
        String path = Global.phoneStoragePath + "/" + constructImageFileName(item);
        return BitmapFactory.decodeFile(path);
    }

    public static Set<String> getGroupsSet() {
        return PreferenceImageDataManager.getGroupsSet();
    }
    public static Set<String> getFoldersSet() { return PreferenceImageDataManager.getFoldersSet(); }
    public static Set<String> getImagesSet() { return PreferenceImageDataManager.getImagesSet(); }


    private static void makeError() {
        System.out.println("Couldn't save");

    }

    private static String constructImageFileName(ImageItem item) {
        return "GI#" + Integer.toHexString(item.getParentFolder().getId()) +
                "#"  + Integer.toHexString(item.getId());
    }

    private static String constructFilePrefName(ImageItem item) {
        return  "GI#" + Integer.toHexString(item.getParentFolder().getParentGroup().getId()) +
                "#" + Integer.toHexString(item.getParentFolder().getId()) +
                "#" + item.getParentFolder().getTitle();
    }
    private static String constructGroupPrefName(ImageItem item) {
        return "GI#" + Integer.toHexString(item.getParentFolder().getParentGroup().getId()) +
                "#" + item.getParentFolder().getParentGroup().getTitle() +
                "#" + item.getParentFolder().getParentGroup().getDescription();
    }

    public static boolean alreadySaved(ImageItem item) {
        return PreferenceImageDataManager.alreadySaved(constructImageFileName(item));
    }


    private static class PreferenceImageDataManager{

        private static SharedPreferences preferences;
        private static SharedPreferences.Editor editor;
        private static Set<String> imagesSet;
        private static Set<String> foldersSet;
        private static Set<String> groupsSet;


        private static void init() {
            if (!PreferenceImageDataManager.isPreferenceDataInitialized())
                PreferenceImageDataManager.initializePreferenceData();
        }

        static boolean isPreferenceDataInitialized() {
            return imagesSet != null;
        }

        static void initializePreferenceData() {
            System.out.println("init continued");
            preferences = Global.getSharedPreferences(null);



            imagesSet = preferences.getStringSet(PREFERENCE_TAG_IMAGES, new HashSet<String>());
            foldersSet = preferences.getStringSet(PREFERENCE_TAG_FOLDERS, new HashSet<String>());
            groupsSet = preferences.getStringSet(PREFERENCE_TAG_GROUPS, new HashSet<String>());

            //for (String s : groupsSet) {
                    editor = preferences.edit();
                    editor.remove(PREFERENCE_TAG_GROUPS).commit();
                    //groupsSet.remove(s);
                    editor.putStringSet(PREFERENCE_TAG_GROUPS, new HashSet<String>()).commit();
            //}
            /*imagesSet = new HashSet<>();
            foldersSet = new HashSet<>();
            groupsSet = new HashSet<>(); */
            System.out.println(imagesSet.toString());
        }

        static Set<String> getGroupsSet() {
            init();
            return groupsSet;
        }

        static Set<String> getFoldersSet() {
            init();
            return foldersSet;
        }

        static Set<String> getImagesSet() {
            init();
            return imagesSet;
        }

        static boolean alreadySaved(String fileName) {
            init();
            return imagesSet.contains(fileName);
        }

        static void saveFile(String imagePrefName, String filePrefName, String groupPrefName) {
            init();
            editor = preferences.edit();
            editor.remove(PREFERENCE_TAG_IMAGES);
            editor.remove(PREFERENCE_TAG_FOLDERS);
            editor.remove(PREFERENCE_TAG_GROUPS);
            editor.commit();
            imagesSet.add(imagePrefName);
            foldersSet.add(filePrefName);
            groupsSet.add(groupPrefName);
            editor.putStringSet(PREFERENCE_TAG_IMAGES, imagesSet);
            editor.putStringSet(PREFERENCE_TAG_FOLDERS, foldersSet);
            editor.putStringSet(PREFERENCE_TAG_GROUPS, groupsSet);
            if (!editor.commit())
                System.out.println("Editor done goofed");
        }

        static void deleteFile(String fileName) {
            init();
            editor = preferences.edit();
            editor.remove(PREFERENCE_TAG_IMAGES);
            editor.commit();
            imagesSet.remove(fileName);
            editor.putStringSet(PREFERENCE_TAG_IMAGES, imagesSet);
            boolean worked = editor.commit();
            if (!worked)
                System.out.println("Failed to delete file from preferences");
            else
                System.out.println("Successfully deleted file from preferences");
        }

        static void tryToDeleteFolder(int folderId) {
            if (noImageInFolder(folderId)) {
                for (String s : foldersSet) {
                    if (s.split("#")[2].equals(Integer.toHexString(folderId))) {
                        editor = preferences.edit();
                        editor.remove(PREFERENCE_TAG_FOLDERS).commit();
                        foldersSet.remove(s);
                        editor.putStringSet(PREFERENCE_TAG_FOLDERS, foldersSet).commit();
                        break;
                    }
                }
            }
        }

        private static boolean noImageInFolder(int folderId) {
            for (String s : imagesSet)
                if (s.split("#")[1].equals(Integer.toHexString(folderId)))
                    return  false;
            return  true;
        }

        static void tryToDeleteGroup(int groupId) {
            if (noFolderInGroup(groupId)) {
                for (String s : groupsSet) {
                    if (s.split("#")[1].equals(Integer.toHexString(groupId))) {
                        editor = preferences.edit();
                        editor.remove(PREFERENCE_TAG_GROUPS).commit();
                        groupsSet.remove(s);
                        editor.putStringSet(PREFERENCE_TAG_GROUPS, groupsSet).commit();
                        break;
                    }
                }
            }
        }

        private static boolean noFolderInGroup(int groupId) {
            for (String s : foldersSet)
                if (s.split("#")[1].equals(Integer.toHexString(groupId)))
                    return  false;
            return  true;
        }

    }

}
