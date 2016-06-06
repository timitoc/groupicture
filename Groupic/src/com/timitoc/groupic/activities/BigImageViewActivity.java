package com.timitoc.groupic.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.OnSwipeTouchListener;
import com.timitoc.groupic.utils.VolleySingleton;

/**
 * Created by timi on 03.05.2016.
 */
public class BigImageViewActivity extends Activity {

   // ImageView imageView;
    private ImageSwitcher imageSwitcher;
    private int currentIndex;
    String[] requestArray;
    ImageLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_image_view_layout);

        String requestUrl = getIntent().getStringExtra("request_url");
        currentIndex = getIntent().getIntExtra("index", 0);
        requestArray = getIntent().getStringArrayExtra("request_array");

        //imageView = (ImageView) findViewById(R.id.big_image);
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
        loadImage(requestArray[currentIndex]);

        //loader.get(requestUrl, ImageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher));

        imageSwitcher.setOnTouchListener(new OnSwipeTouchListener(BigImageViewActivity.this) {
            public void onSwipeRight() {
                //Toast.makeText(BigImageViewActivity.this, "right", Toast.LENGTH_SHORT).show();
                imageSwitcher.setInAnimation(inRight);
                imageSwitcher.setOutAnimation(outRight);
                currentIndex = (currentIndex + 1 + requestArray.length) % requestArray.length;
                imageSwitcher.setImageResource(R.drawable.ic_launcher);
                loadImage(requestArray[currentIndex]);
            }
            public void onSwipeLeft() {
                //Toast.makeText(BigImageViewActivity.this, "left", Toast.LENGTH_SHORT).show();
                imageSwitcher.setInAnimation(inLeft);
                imageSwitcher.setOutAnimation(outLeft);
                currentIndex = (currentIndex - 1 + requestArray.length) % requestArray.length;
                imageSwitcher.setImageResource(R.drawable.ic_launcher);
                loadImage(requestArray[currentIndex]);
            }
        });
    }

    private void loadImage(String requestUrl)
    {
        loader.get(requestUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                BitmapDrawable drawable = new BitmapDrawable(response.getBitmap());
                imageSwitcher.setInAnimation(BigImageViewActivity.this, android.R.anim.fade_in);
                imageSwitcher.setOutAnimation(BigImageViewActivity.this, android.R.anim.fade_out);
                imageSwitcher.setImageDrawable(drawable);
                System.out.println("Image loaded ");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error " + error.getMessage());
            }
        });

    }
}
