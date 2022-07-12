package com.smartbooster.junkcleaner.utils

import java.io.File

//var mInterstitialAd: PublisherInterstitialAd? = null
var listener: (() -> Unit)? = null

fun deleteRecursively(file: File?) {
    file?.let {
        it.deleteRecursively()
    }
}