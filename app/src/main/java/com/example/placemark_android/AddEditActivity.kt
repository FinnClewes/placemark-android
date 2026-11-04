package com.example.placemark_android

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)

        descriptionInput = findViewById(R.id.descriptionInput)

        xInput = findViewById(R.id.xInput)

        yInput = findViewById(R.id.yInput)

        val saveButton = findViewById<Button>(R.id.saveButton)

        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }

        if (editingId != null) {

            findViewById<TextView>(R.id.formTitle)
                .text = "Edit Mark"

            loadExistingMark(editingId!!)
        }

        saveButton.setOnClickListener {
            Timber.i("Save button clicked")
            saveMark()
        }

        cancelButton.setOnClickListener {
            Timber.i("Cancel button clicked")
            finish()
        }
    }

    private fun loadExistingMark(id: Long) {

        val mark = AppData.placedMarks.findOne(id)

        if (mark == null) {
            Timber.e("No mark found with id=%d", id)
            Toast.makeText(
                this,
                "Mark not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.description)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())
    }

    private fun saveMark() {

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            Timber.w("Save failed: title is empty")
            titleInput.error = "Title is required"
            return
        }

        val x = xInput.text.toString().toFloatOrNull()

        if (x == null) {
            Timber.w("Save failed: invalid X value '%s'", xInput.text)
            xInput.error = "Enter a valid number"
            return
        }

        val y = yInput.text.toString().toFloatOrNull()

        if (y == null) {
            Timber.w("Save failed: invalid Y value '%s'", yInput.text)
            yInput.error = "Enter a valid number"
            return
        }

        if (editingId == null) {

            val mark = PlacemarkModel(
                title = title,
                description = description,
                x = x,
                y = y
            )

            AppData.placedMarks.create(mark)

            Timber.i("Mark created: title=%s, x=%f, y=%f", mark.title, mark.x, mark.y)

            Toast.makeText(
                this,
                "Mark created",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val mark = PlacemarkModel(
                id = editingId!!,
                title = title,
                description = description,
                x = x,
                y = y
            )

            AppData.placedMarks.update(mark)

            Timber.i("Mark updated: id=%d, title=%s", mark.id, mark.title)

            Toast.makeText(
                this,
                "Mark updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}