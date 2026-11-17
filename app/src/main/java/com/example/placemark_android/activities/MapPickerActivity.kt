package com.example.placemark_android.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import com.example.placemark_android.R
import com.example.placemark_android.data.AppData

class MapPickerActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null

    private var markerTitle: String = "Selected location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "PlacemarkLab-SETU-FinnClewes (student project)"
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val lat = intent.getDoubleExtra("latitude", 52.245954)
        val lon = intent.getDoubleExtra("longitude", -7.139539)
        val start = GeoPoint(lat, lon)

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)

        markerTitle = intent.getStringExtra("title")
            ?.takeIf { it.isNotBlank() }
            ?: "Selected location"

        setSelectedPoint(start)
        showExistingMarkers()
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point)
                return true
            }

            override fun longPressHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point)
                return true
            }
        }
        map.overlays.add(MapEventsOverlay(receiver))

        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
            val point = selectedPoint ?: return@setOnClickListener
            val result = intent
                .putExtra("latitude", point.latitude)
                .putExtra("longitude", point.longitude)

            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    private fun setSelectedPoint(point: GeoPoint) {
        selectedPoint = point

        if (marker == null) {
            marker = Marker(map).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(this@MapPickerActivity, R.drawable.ic_marker_selected)
            }
            map.overlays.add(marker)
        }

        marker?.position = point
        marker?.title = markerTitle
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }

    private fun showExistingMarkers() {
        val placemarks = AppData.placedMarks.findAll()

        placemarks.forEach { placemark ->
            val point = GeoPoint(
                placemark.x.toDouble(), // latitude
                placemark.y.toDouble()  // longitude
            )

            val marker = Marker(map).apply {
                position = point
                title = placemark.title
                snippet = placemark.desc
                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )
                icon = ContextCompat.getDrawable(this@MapPickerActivity, R.drawable.ic_marker_existing)
            }

            map.overlays.add(marker)
        }

        map.invalidate()
    }
}