package com.hongyue.app.muse

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.hongyue.app.muse.loader.MuseMediaEventListener
import com.hongyue.app.muse.model.MediaEntity
import com.hongyue.app.muse.utils.DoubleUtils
import com.hongyue.app.muse.widget.PhoenixLoadingDialog

open class BaseMuseActivity : FragmentActivity() {
    protected lateinit var mContext: Context
    protected lateinit var option: PhoenixOption

    protected var themeColor: Int = 0
    protected var spanCount: Int = 0
    protected var maxSelectNum: Int = 0
    protected var minSelectNum: Int = 0
    protected var fileType: Int = 0
    protected var videoFilterTime: Int = 0
    protected var mediaFilterSize: Int = 0
    protected var recordVideoTime: Int = 0
    protected var isGif: Boolean = false
    protected var enableCamera: Boolean = false
    protected var enablePreview: Boolean = false
    protected var enableCompress: Boolean = false
    protected var checkNumMode: Boolean = false
    protected var openClickSound: Boolean = false
    protected var previewEggs: Boolean = false
    protected var savePath: String = ""
    protected var mMediaEventListener: MuseMediaEventListener? = null

    protected val loadingDialog: PhoenixLoadingDialog by lazy { PhoenixLoadingDialog(mContext) }

    protected lateinit var mediaList: MutableList<MediaEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)//设置成全屏模式
        }
        hideVirtualKey()
        mContext = this
    }

    protected fun startActivity(clz: Class<*>, bundle: Bundle, requestCode: Int) {
        if (!DoubleUtils.isFastDoubleClick) {
            val intent = Intent()
            intent.setClass(this, clz)
            intent.putExtras(bundle)
            startActivityForResult(intent, requestCode)
        }
    }

    protected open fun showToast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
    }

    /**
     * show loading loadingDialog
     */
    protected fun showLoadingDialog() {
        if (!isFinishing) {
            dismissLoadingDialog()
            loadingDialog.show()
        }
    }

    /**
     * dismiss loading loadingDialog
     */
    protected fun dismissLoadingDialog() {
        try {
            if (loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun processMedia(mediaList: MutableList<MediaEntity>) {
//        val enableCompress = option.isEnableCompress
//
//        if (!enableCompress) {
//            mMediaEventListener?.onMuseMedia(mediaList)
//            closeActivity()
//            return
//        }

        mMediaEventListener?.onMuseMedia(mediaList)
        closeActivity()
        return

    }



    /**
     * Close Activity
     */
    protected open fun closeActivity() {
        finish()
        overridePendingTransition(0, R.anim.phoenix_activity_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoadingDialog()
    }

    protected fun tintDrawable(resId: Int, color: Int): Drawable {
        val drawable = ContextCompat.getDrawable(this, resId)!!
        DrawableCompat.setTint(drawable, color)
        return drawable
    }

    fun setupConfig() {
        themeColor = option.theme
        enableCamera = option.isEnableCamera
        fileType = option.fileType
        mediaList = option.pickedMediaList
        spanCount = option.spanCount
        isGif = option.isEnableGif
        maxSelectNum = option.maxPickNumber
        minSelectNum = option.minPickNumber
        enablePreview = option.isEnablePreview
        checkNumMode = option.isPickNumberMode
        openClickSound = option.isEnableClickSound
        videoFilterTime = option.videoFilterTime
        mediaFilterSize = option.mediaFilterSize
        recordVideoTime = option.recordVideoTime
        enableCompress = option.isEnableCompress
        previewEggs = option.isPreviewEggs
        savePath = option.savePath
        mMediaEventListener = MuseGallery.getMuseMediaEvent()

    }

    private fun hideVirtualKey() {
        val window = window
        val params = window.attributes
        params.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.attributes = params
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //判断是否有焦点
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        }
    }
}