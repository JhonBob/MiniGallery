package com.hongyue.app.muse.loader;

import com.hongyue.app.muse.model.MediaEntity;

import java.util.List;

public interface MuseMediaEventListener {

    public void onMuseMedia(List<MediaEntity> medias);

}
