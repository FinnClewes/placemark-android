package com.example.placemark_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTitle)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val viewMarksButton =
            findViewById<Button>(R.id.viewMarksButton)

        val addMarkButton =
            findViewById<Button>(R.id.addMarkButton)

        viewMarksButton.setOnClickListener {
            Timber.i("View Marks clicked, opening MarkListActivity")
            startActivity(
                Intent(this, MarkListActivity::class.java)
            )
        }

        addMarkButton.setOnClickListener {
            Timber.i("Add Mark clicked, opening AddEditActivity")
            startActivity(
                Intent(this, AddEditActivity::class.java)
            )
        }

    }
}