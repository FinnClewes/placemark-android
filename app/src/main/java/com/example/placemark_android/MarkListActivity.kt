package com.example.placemark_android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class MarkListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mark_list)

        recyclerView =
            findViewById(R.id.marksRecyclerView)

        adapter = PlacedMarkAdapter(
            marks = AppData.placedMarks.findAll(),

            listener = object : OnMarkListener {

                override fun onMarkClick(mark: PlacemarkModel) {

                    editMark(mark)

                }

                override fun onMarkLongClick(mark: PlacemarkModel): Boolean {

                    deleteMark(mark)

                    return true

                }
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {

            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {

            adapter.updateMarks(
                AppData.placedMarks.findAll()
            )
        }
    }

    private fun editMark(mark: PlacemarkModel) {

        val intent =
            Intent(this, AddEditActivity::class.java)

        intent.putExtra("id", mark.id)

        startActivity(intent)

        Timber.d("Editing mark id=${mark.id}, title=${mark.title}")
    }

    private fun deleteMark(mark: PlacemarkModel) {

        AppData.placedMarks.delete(mark.id)

        adapter.updateMarks(
            AppData.placedMarks.findAll()
        )

        Timber.d("Deleting mark id=${mark.id}, title=${mark.title}")
    }
}