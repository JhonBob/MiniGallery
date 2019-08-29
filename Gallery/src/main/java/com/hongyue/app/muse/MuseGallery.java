package com.hongyue.app.muse;

import android.content.Context;
import androidx.annotation.NonNull;

import com.hongyue.app.muse.loader.MuseMediaEventListener;
import com.hongyue.app.muse.model.MediaEntity;
import com.hongyue.app.muse.model.MimeType;

import java.util.List;

public class MuseGallery {

    private static volatile MuseConfig config;
    private static MuseMediaEventListener mListener;
    private Context mContext;
    private PhoenixOption option;

    public MuseGallery(Context context) {
        this.mContext = context;
    }

    public static MuseConfig config() {
        if (config == null) {
            synchronized (MuseGallery.class) {
                if (config == null) {
                    config = new MuseConfig();
                }
            }
        }
        return config;
    }

    public static MuseGallery with(@NonNull Context context) {
        MuseGallery instance = new MuseGallery(context);
        return instance;
    }

    public MuseGallery setMuseMediaEvent(MuseMediaEventListener listener){
        this.mListener = listener;
        return this;
    }

    public MuseGallery composeOption(PhoenixOption option){
        this.option = option;
        return this;
    }

    public MuseGallery pickedMediaList(List<MediaEntity> val){
        this.option.pickedMediaList(val);
        return this;
    }

    public static MuseMediaEventListener getMuseMediaEvent(){
        return mListener;
    }

    public void openMuseGallery(){
        PickerActivity.Companion.startAction(mContext, buildOption() );
    }

    private PhoenixOption buildOption(){
        return option;
    }

    public static PhoenixOption withOption(){
        return new PhoenixOption();
    }



    /**
     *  Description:  以下为自定义Option
     *  Author: Charlie
     *  Data: 2019/3/27  11:34
     *  Declare: None
     */

    public static PhoenixOption oPtionSingle(){

        return withOption()
                .theme(PhoenixOption.THEME_DEFAULT)// 主题
                .fileType(MimeType.ofImage())//显示的文件类型图片、视频、图片和视频
                .ebleRecord(false)
                .maxPickNumber(1)// 最大选择数量
                .minPickNumber(0)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(true)// 是否开启预览
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(true)// 是否开启压缩
                .compressPictureFilterSize(1024)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false);// 是否开启点击声音

    }

    public static PhoenixOption oPtionMultiple(int max){
        return withOption()
                .theme(PhoenixOption.THEME_DEFAULT)// 主题
                .fileType(MimeType.ofImage())//显示的文件类型图片、视频、图片和视频
                .ebleRecord(false)
                .maxPickNumber(max)// 最大选择数量
                .minPickNumber(0)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(true)// 是否开启预览
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(true)// 是否开启压缩
                .compressPictureFilterSize(1024)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false);// 是否开启点击声音
    }


    public static PhoenixOption oPtionMultiple(){
        return withOption()
                .theme(PhoenixOption.THEME_DEFAULT)// 主题
                .fileType(MimeType.ofImage())//显示的文件类型图片、视频、图片和视频
                .ebleRecord(false)
                .minPickNumber(0)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(true)// 是否开启预览
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(true)// 是否开启压缩
                .compressPictureFilterSize(1024)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false);// 是否开启点击声音
    }

}
