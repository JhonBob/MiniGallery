package com.hongyue.app.muse

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.hongyue.app.camera.CameraActivity
import com.hongyue.app.camera.CameraConstant
import com.hongyue.app.muse.PhoenixOption.THEME_DEFAULT
import com.hongyue.app.muse.adapter.PickerAdapter
import com.hongyue.app.muse.adapter.PickerAlbumAdapter
import com.hongyue.app.muse.loader.MediaLoader
import com.hongyue.app.muse.model.MediaEntity
import com.hongyue.app.muse.model.MediaFolder
import com.hongyue.app.muse.model.MimeType
import com.hongyue.app.muse.utils.ScreenUtil
import com.hongyue.app.muse.utils.StringUtils
import com.hongyue.app.muse.widget.FolderPopWindow
import com.hongyue.app.muse.widget.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_picker.*
import kotlinx.android.synthetic.main.include_title_bar.*
import java.util.*

class PickerActivity : BaseMuseActivity() , PickerAdapter.OnPickChangedListener, View.OnClickListener, PickerAlbumAdapter.OnItemClickListener{

    private val TAG = PickerActivity::class.java.simpleName

    private lateinit var pickAdapter: PickerAdapter
    private var allMediaList: MutableList<MediaEntity> = ArrayList()
    private var allFolderList: MutableList<MediaFolder> = ArrayList()

    private var isAnimation = false
    private lateinit var folderWindow: FolderPopWindow
    private var animation: Animation? = null
    private lateinit var mediaLoader: MediaLoader
    private val REQUEST_CODE = 0x000002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)
        option = intent.getParcelableExtra("option")


        requirePermission()

    }


    private fun requirePermission() {
        if (Build.VERSION.SDK_INT > 15) {
            val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)

            val permissionsToRequest = ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        permissionsToRequest.toTypedArray(), CameraConstant.REQUEST_CODE_CAMERA_PERMISSIONS)
            } else
                initView()
        } else {
            initView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            initView()
        }
    }


    fun initView(){

        setupConfig()
        setupView()
        setupData()
    }


    /**
     * init views
     */
    private fun setupView() {
        pickRlTitle.setBackgroundColor(themeColor)

        if (themeColor == THEME_DEFAULT) {
            rl_bottom.setBackgroundColor(themeColor)

        } else {
            rl_bottom.setBackgroundColor(Color.WHITE)
            pickTvPreview.setTextColor(themeColor)
            pickLlOk.background = tintDrawable(R.drawable.phoenix_shape_complete_background, themeColor)
        }

        isNumberComplete()
        pickTvTitle.text = if (fileType == MimeType.ofAudio()) getString(R.string.picture_all_audio) else getString(R.string.picture_camera_roll)
        pick_tv_empty.text = if (fileType == MimeType.ofAudio()) getString(R.string.picture_audio_empty) else getString(R.string.picture_empty)
        StringUtils.tempTextFont(pick_tv_empty, fileType)

        val titleText = pickTvTitle.getText().toString().trim { it <= ' ' }
        if (enableCamera) {
            enableCamera = StringUtils.isCamera(titleText)
        }

        folderWindow = FolderPopWindow(this, fileType)
        folderWindow.setPictureTitleView(pickTvTitle)
        folderWindow.setOnItemClickListener(this)

        pickTvPreview.setOnClickListener(this)
        pickTvBack.setOnClickListener(this)
        pickTvCancel.setOnClickListener(this)
        pickLlOk.setOnClickListener(this)
        pickTvTitle.setOnClickListener(this)
    }

    private fun setupData() {
        pickRecyclerView.setHasFixedSize(true)
        pickRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount,
            ScreenUtil.dip2px(this, 2f), false)
        )
        pickRecyclerView.layoutManager = GridLayoutManager(this, spanCount) as RecyclerView.LayoutManager?
        (pickRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        pickAdapter = PickerAdapter(mContext, option)
        pickRecyclerView.adapter = pickAdapter
        pickAdapter.setOnPickChangedListener(this)
        pickAdapter.setPickMediaList(mediaList)
        changeImageNumber(mediaList)

        mediaLoader = MediaLoader(
            this,
            fileType,
            isGif,
            videoFilterTime.toLong(),
            mediaFilterSize
        )
        readLocalMedia()
    }

    /**
     * none number style
     */
    @SuppressLint("StringFormatMatches")
    private fun isNumberComplete() {
        pickTvOk.text = getString(R.string.picture_please_select)
        animation = AnimationUtils.loadAnimation(this, R.anim.phoenix_window_in)
    }

    /**
     * get MediaEntity s
     */
    private fun readLocalMedia() {
        mediaLoader.loadAllMedia(object : MediaLoader.LocalMediaLoadListener {
            override fun loadComplete(folders: MutableList<MediaFolder>) {
                if (folders.size > 0) {
                    allFolderList = folders
                    val folder = folders[0]
                    folder.isChecked = true
                    val localImg = folder.images
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size >= allMediaList.size) {
                        allMediaList = localImg
                        folderWindow.bindFolder(folders)
                    }
                }
                if (pickAdapter != null) {
                    if (allMediaList == null) {
                        allMediaList = ArrayList()
                    }
                    pickAdapter.setAllMediaList(allMediaList)
                    pick_tv_empty.visibility = if (allMediaList.size > 0) View.INVISIBLE else View.VISIBLE
                }
                dismissLoadingDialog()
            }
        })
    }

    @SuppressLint("StringFormatMatches")
    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.pickTvBack || id == R.id.pickTvCancel) {
            if (folderWindow.isShowing) {
                folderWindow.dismiss()
            } else {
                closeActivity()
            }
        }
        if (id == R.id.pickTvTitle) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss()
            } else {
                if (allMediaList.size > 0) {
                    folderWindow.showAsDropDown(pickRlTitle)
                    val selectedImages = pickAdapter.getPickMediaList()
                    folderWindow.notifyDataCheckedStatus(selectedImages)
                }
            }
        }

        if (id == R.id.pickTvPreview) {
            val pickedImages = pickAdapter.getPickMediaList()
//            Navigator.showPreviewView(this, option, pickedImages, pickedImages, 0)
        }

        if (id == R.id.pickLlOk) {
            val images = pickAdapter.getPickMediaList()
            val pictureType = if (images.size > 0) images[0].mimeType else ""
            val size = images.size
            val eqImg = !TextUtils.isEmpty(pictureType) && pictureType.startsWith(com.hongyue.app.muse.PhoenixConstant.IMAGE)

            // 如果设置了图片最小选择数量，则判断是否满足条件
            if (minSelectNum > 0) {
                if (size < minSelectNum) {
                    @SuppressLint("StringFormatMatches") val str = if (eqImg)
                        getString(R.string.picture_min_img_num, minSelectNum)
                    else
                        getString(R.string.phoenix_message_min_number, minSelectNum)
                    showToast(str)
                    return
                }
            }
            processMedia(images)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == 101){

            val filePath = data?.getStringExtra("path")
            val type = data?.getIntExtra("type", -1)

            if (type == 0){
                //图像
                val mediaEntity = MediaEntity.newBuilder()
                        .localPath(filePath)
                        .fileType(MimeType.ofImage())
                        .mimeType(MimeType.createImageType(filePath))
                        .build()
                allMediaList.add(0,mediaEntity)
            } else{
                //图像
                val mediaEntity = MediaEntity.newBuilder()
                        .localPath(filePath)
                        .fileType(MimeType.ofImage())
                        .mimeType(MimeType.createImageType(filePath))
                        .build()
                allMediaList.add(0,mediaEntity)
            }

        }

    }

    override fun onItemClick(folderName: String, images: MutableList<MediaEntity>) {
        pickTvTitle.text = folderName
        pickAdapter.setAllMediaList(images)
        folderWindow.dismiss()
    }

    override fun onTakePhoto() {
        startCamera()
    }

    override fun onChange(selectImages: List<MediaEntity>) {
        changeImageNumber(selectImages)
    }

    override fun onPictureClick(mediaEntity: MediaEntity, position: Int) {
//        Navigator.showPreviewView(this, option, pickAdapter.getAllMediaList(), pickAdapter.getPickMediaList(), position)
    }

    /**
     * change image selector state

     * @param selectImages
     */
    @SuppressLint("StringFormatMatches")
    private fun changeImageNumber(selectImages: List<MediaEntity>) {
        val enable = selectImages.isNotEmpty()
        if (enable) {
            pickLlOk.isEnabled = true
            pickLlOk.alpha = 1F
            pickTvPreview.isEnabled = true
            pickTvPreview.setTextColor(if (themeColor == THEME_DEFAULT) ContextCompat.getColor(mContext, R.color.green) else themeColor)
            if (!isAnimation) {
                pickTvNumber.startAnimation(animation)
            }
            pickTvNumber.visibility = View.VISIBLE
            pickTvNumber.text = String.format("(%d)", selectImages.size)
            pickTvOk.text = getString(R.string.picture_completed)
            isAnimation = false
        } else {
            pickLlOk.isEnabled = false
            pickLlOk.alpha = 0.7F
            pickTvPreview.isEnabled = false
            pickTvPreview.setTextColor(ContextCompat.getColor(mContext, R.color.color_gray_1))
            pickTvNumber.visibility = View.GONE
            pickTvOk.text = getString(R.string.picture_please_select)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        closeActivity()
    }

    override fun onDestroy() {
        super.onDestroy()

        animation?.cancel()
    }

    private fun startCamera() {
        var  intent = Intent()
        intent.setClass(mContext, CameraActivity::class.java)
        intent.putExtra("enableRecord", option.isRecordEnable)
        startActivityForResult(intent, 0x000002)
        overridePendingTransition(R.anim.phoenix_activity_in, 0)
    }

    companion object {

        fun startAction(mContext: Context, option: PhoenixOption){
            var intent = Intent()
            intent.putExtra("option", option)
            intent.setClass(mContext, PickerActivity::class.java)
            mContext.startActivity(intent)
        }

    }


}
