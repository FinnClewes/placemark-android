package com.example.placemark_android.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.placemark_android.data.AppData
import com.example.placemark_android.models.PlacemarkModel
import com.example.placemark_android.R
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    private lateinit var imagePreview: ImageView
    private var selectedImageUri: Uri? = null

    val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                // Keep access to the document URI for later use, when supported.
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some providers may not grant persistable access.
                }

                selectedImageUri = uri
                imagePreview.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)

        descriptionInput = findViewById(R.id.descriptionInput)

        xInput = findViewById(R.id.xInput)

        yInput = findViewById(R.id.yInput)

        imagePreview = findViewById(R.id.imageViewAdd)

        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

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
        descriptionInput.setText(mark.desc)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())

        selectedImageUri = mark.imageUri?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            imagePreview.setImageURI(selectedImageUri)
        } else {
            imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }
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

        try {
            if (editingId == null) {
                val mark = PlacemarkModel(
                    title = title,
                    desc = description,
                    x = x,
                    y = y,
                    imageUri = selectedImageUri?.toString()
                )
                AppData.placedMarks.create(mark)
                Timber.i("Mark created: id=%d, title=%s", mark.id, mark.title)
                Toast.makeText(this, "Mark created", Toast.LENGTH_SHORT).show()
            } else {
                val mark = PlacemarkModel(
                    id = editingId!!,
                    title = title,
                    desc = description,
                    x = x,
                    y = y,
                    imageUri = selectedImageUri?.toString()
                )
                AppData.placedMarks.update(mark)
                Timber.i("Mark updated: id=%d, title=%s", mark.id, mark.title)
                Toast.makeText(this, "Mark updated", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IllegalArgumentException) {
            Timber.w("Save rejected: %s", e.message)
            Toast.makeText(this, e.message ?: "Please check the entered values.", Toast.LENGTH_LONG).show()
            xInput.error = "Must be a decimal between -90.0 and 90.0"
            yInput.error = "Must be a decimal between -180.0 and 180.0"
            return
        }

        finish()
    }
}