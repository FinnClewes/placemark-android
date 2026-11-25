package com.example.placemark_android.activities

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.placemark_android.R
import com.example.placemark_android.data.AppData
import com.example.placemark_android.models.PlacemarkModel
import org.osmdroid.config.Configuration

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.CopyrightOverlay

class MapListActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "PlacemarkLab-SETU-FinnClewes (student project)"
        setContentView(R.layout.activity_map_list)

        // Initialise views
        mapView = findViewById(R.id.mapList)
        txtTitle = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace = findViewById(R.id.mapImageView)

        // Configure OpenStreetMap
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Show map attribution
        mapView.overlays.add(CopyrightOverlay(this))

        // Load placemarks
        loadMarkers()
    }

    private fun loadMarkers() {

        val placemarks = AppData.placedMarks.findAll()

        placemarks.forEach { placemark ->

            val position = GeoPoint(
                placemark.x.toDouble(),
                placemark.y.toDouble()
            )

            val marker = Marker(mapView)

            marker.position = position
            marker.title = placemark.title
            marker.snippet = placemark.desc

            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            // Handle marker selection
            marker.setOnMarkerClickListener { clickedMarker, _ ->

                displayPlace(placemark)

                // Return true to consume the click
                true
            }

            mapView.overlays.add(marker)
        }

        // Centre map on first placemark
        placemarks.firstOrNull()?.let { first ->

            mapView.controller.setZoom(12.0)

            mapView.controller.setCenter(
                GeoPoint(first.x.toDouble(), first.y.toDouble())
            )
        }

        mapView.invalidate()
    }

    private fun displayPlace(placemark: PlacemarkModel) {

        txtTitle.text = placemark.title

        txtDescription.text = placemark.desc

        if (placemark.imageUri != null) {
            imgPlace.setImageURI(
                Uri.parse(placemark.imageUri)
            )
        } else {
            imgPlace.setImageDrawable(null)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()

        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
    }
}
