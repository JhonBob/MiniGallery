package com.hongyue.app.muse;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hongyue.app.muse.adapter.MediaAdapter;
import com.hongyue.app.muse.loader.MuseMediaEventListener;
import com.hongyue.app.muse.model.MediaEntity;

import java.util.List;

/**
 *  Description:  多媒体选择测试类
 *  Author: Charlie
 *  Data: 2019/5/13  15:03
 *  Declare: None
 */

public class MediaFragment extends Fragment implements MediaAdapter.OnAddMediaListener
        , View.OnClickListener {

    private int REQUEST_CODE = 0x000111;
    private MediaAdapter mMediaAdapter;

    public static MediaFragment newInstance(){
        return new MediaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_phoenix_demo, container, false);
        view.findViewById(R.id.btn_compress_picture).setOnClickListener(this);
        view.findViewById(R.id.btn_compress_video).setOnClickListener(this);
        view.findViewById(R.id.btn_take_picture).setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        mMediaAdapter = new MediaAdapter(getActivity(), this);
        recyclerView.setAdapter(mMediaAdapter);
        mMediaAdapter.setOnItemClickListener(new MediaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v, List<String> images) {
                if (mMediaAdapter.getData().size() > 0) {
                   //预览，后期有需要再弄

                }
            }
        });
        return view;
    }

    @Override
    public void onaddMedia() {
        MuseGallery.with(getActivity())
                .composeOption(MuseGallery.oPtionMultiple())
                .pickedMediaList(mMediaAdapter.getData())
                .setMuseMediaEvent(new MuseMediaEventListener() {
                    @Override
                    public void onMuseMedia(List<MediaEntity> medias) {
                        mMediaAdapter.setData(medias);
                    }
                })
                .openMuseGallery();
    }

    @Override
    public void onClick(View v) {
    }
}
