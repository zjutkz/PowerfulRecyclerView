package com.example.kangzhe.sample;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by kangzhe on 16/3/24.
 */
public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        ImageLoader.getInstance().init(configuration);
    }
}
