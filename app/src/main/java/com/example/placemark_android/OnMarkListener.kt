package com.example.placemark_android

interface OnMarkListener {
    fun onMarkClick(mark: PlacemarkModel)
    fun onMarkLongClick(mark: PlacemarkModel): Boolean
}