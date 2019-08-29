package com.hongyue.app.muse;

import com.hongyue.app.muse.loader.MuseImageLoader;

public class MuseConfig {

    private MuseImageLoader imageLoader;

    public MuseImageLoader getImageLoader() {
        return imageLoader;
    }

    public MuseConfig imageLoader(MuseImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }


}
