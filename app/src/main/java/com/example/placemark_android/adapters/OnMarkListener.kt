package com.example.placemark_android.adapters

import com.example.placemark_android.models.PlacemarkModel

interface OnMarkListener {
    fun onMarkClick(mark: PlacemarkModel)
    fun onMarkLongClick(mark: PlacemarkModel): Boolean
}