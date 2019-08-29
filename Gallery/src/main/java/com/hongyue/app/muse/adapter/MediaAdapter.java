package com.hongyue.app.muse.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hongyue.app.muse.MuseGallery;
import com.hongyue.app.muse.PhoenixConstant;
import com.hongyue.app.muse.R;
import com.hongyue.app.muse.model.MediaEntity;
import com.hongyue.app.muse.model.MimeType;
import com.hongyue.app.muse.utils.DateUtils;
import com.hongyue.app.muse.utils.DebugUtil;
import com.hongyue.app.muse.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  Description:  图片选择适配器
 *  Author: Charlie
 *  Data: 2019/5/13  14:36
 *  Declare: None
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private static final int TYPE_ADD = 1;
    private static final int TYPE_MEDIA = 2;

    private List<MediaEntity> mMediaList = new ArrayList<>();
    private OnAddMediaListener mOnAddMediaListener;
    private OnDeleteItemListener mOnDeleteItemListener;
    private Context mContext;

    public interface OnAddMediaListener {
        void onaddMedia();
    }

    public MediaAdapter(Context mContext,OnAddMediaListener mOnAddMediaListener) {
        this.mContext = mContext;
        this.mOnAddMediaListener = mOnAddMediaListener;
    }

    public void setData(List<MediaEntity> mediaList) {
        if (mediaList != null && mediaList.size() > 0) {
            mMediaList = mediaList;
        }
        notifyDataSetChanged();
    }

    public List<MediaEntity> getData() {
        return mMediaList;
    }

    public List<String> getStringData(){
        List<String> images = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            images.add(getData().get(i).getLocalPath());
        }
        return images;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPicture;
        LinearLayout llDelete;
        TextView tvDuration;

        ViewHolder(View view) {
            super(view);
            ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
            llDelete = (LinearLayout) view.findViewById(R.id.llDelete);
            tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaList.size() == 0 ? 1 : mMediaList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mMediaList.size() || mMediaList.size() == 0) {
            return TYPE_ADD;
        } else {
            return TYPE_MEDIA;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_media, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == TYPE_ADD) {
            viewHolder.ivPicture.setImageResource(R.drawable.ic_add_media);
            viewHolder.ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMediaList.size() < 9){ //fix : 9张限制
                        mOnAddMediaListener.onaddMedia();
                    } else {
                        Toast.makeText(mContext, "嘿嘿，不能选更多了亲",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // fix : 9张限制
            viewHolder.ivPicture.setVisibility(mMediaList.size() < 9 ? View.VISIBLE : View.GONE);
            viewHolder.llDelete.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.llDelete.setVisibility(View.VISIBLE);
            viewHolder.llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = viewHolder.getAdapterPosition();
                    if (index != RecyclerView.NO_POSITION) {
                        if (mOnDeleteItemListener != null) {
                            mOnDeleteItemListener.onDeleteItem(index);
                        }
                        mMediaList.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, mMediaList.size());
                        DebugUtil.INSTANCE.i("delete position:", index + "--->remove after:" + mMediaList.size());
                    }
                }
            });
            MediaEntity mediaEntity = mMediaList.get(position);
            String path = mediaEntity.getFinalPath();
            int fileType = MimeType.getFileType(mediaEntity.getMimeType());
            long duration = mediaEntity.getDuration();
            viewHolder.tvDuration.setVisibility(fileType == PhoenixConstant.TYPE_VIDEO
                    ? View.VISIBLE : View.GONE);
            if(fileType == MimeType.ofVideo()){
                Drawable drawable = ContextCompat.getDrawable(viewHolder.ivPicture.getContext(), R.drawable.phoenix_video_icon);
                StringUtils.INSTANCE.modifyTextViewDrawable(viewHolder.tvDuration, drawable, 0);
                viewHolder.tvDuration.setText(DateUtils.INSTANCE.timeParse(duration));
            }
            MuseGallery.config()
                    .getImageLoader()
                    .loadImage(mContext, viewHolder.ivPicture, path, PhoenixConstant.IMAGE_PROCESS_TYPE_DEFAULT);
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        if (mItemClickListener != null){
                            List<String> imgList = new ArrayList<>();
                            for (int i = 0; i < mMediaList.size(); i++) {
                                imgList.add(mMediaList.get(i).getLocalPath());
                                Log.i("压缩后要上传的图片路径", imgList.get(i));
                            }
                            mItemClickListener.onItemClick(adapterPosition, v, imgList);
                        }
                    }
                });
            }
        }
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v, List<String> images);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnDeleteItemListener {
        void onDeleteItem(int index);
    }

    public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        this.mOnDeleteItemListener = listener;
    }
}
