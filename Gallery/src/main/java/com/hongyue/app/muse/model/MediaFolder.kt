package com.hongyue.app.muse.model

import java.io.Serializable

data class MediaFolder(var name: String,
                       var path: String,
                       var firstImagePath: String,
                       var imageNumber: Int,
                       var checkedNumber: Int,
                       var isChecked: Boolean,
                       var images: MutableList<MediaEntity>) : Serializable