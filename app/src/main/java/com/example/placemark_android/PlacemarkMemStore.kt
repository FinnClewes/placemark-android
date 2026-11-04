package com.example.placemark_android

import java.util.concurrent.atomic.AtomicLong
import timber.log.Timber

class PlacemarkMemStore {
    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacemarkModel> {
        return placemarks
    }

    fun create(placemark: PlacemarkModel) {
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        Timber.i("Created placemark: id=%d, title=%s", placemark.id, placemark.title)
    }

    fun update(placemark: PlacemarkModel): Boolean {
        val foundPlacemark = findOne(placemark.id)
        return if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title
            foundPlacemark.description = placemark.description
            foundPlacemark.x = placemark.x
            foundPlacemark.y = placemark.y
            Timber.i("Updated placemark: id=%d, title=%s", placemark.id, placemark.title)
            true
        } else {
            Timber.w("Update failed: no placemark with id=%d", placemark.id)
            false
        }
    }

    fun delete(id: Long): Boolean {
        val foundPlacemark = findOne(id)
        return if (foundPlacemark != null) {
            placemarks.remove(foundPlacemark)
            Timber.i("Deleted placemark: id=%d", id)
            true
        } else {
            Timber.w("Delete failed: no placemark with id=%d", id)
            false
        }
    }

    fun findOne(id: Long): PlacemarkModel? {
        return placemarks.find { p -> p.id == id }
    }
}