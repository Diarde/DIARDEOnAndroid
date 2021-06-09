package com.studiocinqo.diardeonandroid.ui.fragments.result.photos

import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment

abstract class PhotoBaseFragment : DiardeBaseFragment() {

    abstract fun adviseActionBar(photoIDs: List<String>)

    abstract val editable: Boolean

}