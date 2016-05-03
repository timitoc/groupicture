package com.timitoc.groupic.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import com.android.volley.toolbox.ImageLoader;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.VolleySingleton;

/**
 * Created by timi on 03.05.2016.
 */
public class BigImageViewActivity extends Activity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_image_view_layout);

        String requestUrl = getIntent().getStringExtra("request_url");
        imageView = (ImageView) findViewById(R.id.big_image);

        ImageLoader loader = VolleySingleton.getInstance(this).getImageLoader();
        loader.get(requestUrl, ImageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher));
    }
}
