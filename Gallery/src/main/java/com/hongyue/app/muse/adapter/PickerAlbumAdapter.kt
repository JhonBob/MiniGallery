package com.hongyue.app.muse.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hongyue.app.muse.MuseGallery
import com.hongyue.app.muse.PhoenixConstant
import com.hongyue.app.muse.R
import com.hongyue.app.muse.model.MediaEntity
import com.hongyue.app.muse.model.MediaFolder
import com.hongyue.app.muse.model.MimeType
import java.util.*

class PickerAlbumAdapter(private val context: Context) : RecyclerView.Adapter<PickerAlbumAdapter.ViewHolder>() {

    private var folders: List<MediaFolder> = ArrayList()
    private var mimeType: Int = 0
    private var mContext: Context

    init {
        this.mContext = context
    }

    fun bindFolderData(folders: List<MediaFolder>) {
        this.folders = folders
        notifyDataSetChanged()
    }

    fun setMimeType(mimeType: Int) {
        this.mimeType = mimeType
    }

    val folderData: List<MediaFolder>
        get() {
            if (folders == null) {
                folders = ArrayList<MediaFolder>()
            }
            return folders
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_album_folder, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        val name = folder.name
        val imageNum = folder.imageNumber
        val imagePath = folder.firstImagePath
        val isChecked = folder.isChecked
        val checkedNum = folder.checkedNumber
        holder.tv_sign.visibility = if (checkedNum > 0) View.VISIBLE else View.INVISIBLE
        holder.itemView.isSelected = isChecked
        if (mimeType == MimeType.ofAudio()) {
            holder.first_image.setImageResource(R.drawable.phoenix_audio_placeholder)
        } else {
            MuseGallery.config()
                    .imageLoader
                    .loadImage(context, holder.first_image, imagePath, PhoenixConstant.IMAGE_PROCESS_TYPE_DEFAULT)
        }
        holder.image_num.text = "($imageNum)"
        holder.tv_folder_name.text = name
        holder.itemView.setOnClickListener {
            if (onItemClickListener != null) {
                for (mediaFolder in folders) {
                    mediaFolder.isChecked = false
                }
                folder.isChecked = true
                notifyDataSetChanged()
                onItemClickListener!!.onItemClick(folder.name, folder.images)
            }
        }
    }

    override fun getItemCount(): Int {
        return folders!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var first_image: ImageView
        var tv_folder_name: TextView
        var image_num: TextView
        var tv_sign: TextView

        init {
            first_image = itemView.findViewById(R.id.first_image) as ImageView
            tv_folder_name = itemView.findViewById(R.id.tv_folder_name) as TextView
            image_num = itemView.findViewById(R.id.image_num) as TextView
            tv_sign = itemView.findViewById(R.id.tv_sign) as TextView
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(folderName: String, images: MutableList<MediaEntity>)
    }
}
