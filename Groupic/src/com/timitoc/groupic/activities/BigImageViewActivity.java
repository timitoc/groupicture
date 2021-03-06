package com.timitoc.groupic.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.utils.OnSwipeTouchListener;
import com.timitoc.groupic.utils.SaveLocalManager;
import com.timitoc.groupic.utils.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by timi on 03.05.2016.
 */
public class BigImageViewActivity extends Activity {

   // ImageView imageView;
    private ImageSwitcher imageSwitcher;
    private int currentIndex;
    String[] requestArray;
    ImageLoader loader;
    ArrayList<ImageItem> imageItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_image_view_layout);

        if (savedInstanceState == null)
            currentIndex = getIntent().getIntExtra("index", 0);
        else
            currentIndex = savedInstanceState.getInt("last-index");
        requestArray = getIntent().getStringArrayExtra("request_array");
        imageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra("image_items_list");

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        final Animation inLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        final Animation outLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        final Animation inRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        final Animation outRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        imageSwitcher.setInAnimation(inLeft);
        imageSwitcher.setOutAnimation(outLeft);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                return myView;
            }
        });

        loader = VolleySingleton.getInstance(this).getImageLoader();
        loadImage(currentIndex);

        imageSwitcher.setOnTouchListener(new OnSwipeTouchListener(BigImageViewActivity.this) {
            public void onSwipeRight() {
                imageSwitcher.setInAnimation(inRight);
                imageSwitcher.setOutAnimation(outRight);
                currentIndex = (currentIndex + 1 + requestArray.length) % requestArray.length;

                loadImage(currentIndex);
            }
            public void onSwipeLeft() {
                imageSwitcher.setInAnimation(inLeft);
                imageSwitcher.setOutAnimation(outLeft);
                currentIndex = (currentIndex - 1 + requestArray.length) % requestArray.length;
                loadImage(currentIndex);
            }
        });
    }

    private void loadImage(int currentIndex) {
        if (SaveLocalManager.alreadySaved(imageItems.get(currentIndex)))
            loadImageFromLocal(imageItems.get(currentIndex));
        else
            loadImageFromServer(requestArray[currentIndex]);
    }

    private void loadImageFromLocal(ImageItem imageItem) {
        int oldWidth = 600;
        int oldHeight = 895;
        System.out.println(oldWidth + " " + oldHeight);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), SaveLocalManager.getBitmapFromLocal(imageItem));
        imageSwitcher.setImageDrawable(drawable);
        System.out.println("Image loaded from local");
    }

    private void loadImageFromServer(String requestUrl)
    {
        loader.get(requestUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), response.getBitmap());
                imageSwitcher.setImageDrawable(drawable);
                System.out.println("Image loaded from server");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error " + error.getMessage());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("last-index", currentIndex);
    }
}
