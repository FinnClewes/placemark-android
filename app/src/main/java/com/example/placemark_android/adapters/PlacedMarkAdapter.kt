package com.example.placemark_android.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placemark_android.models.PlacemarkModel
import com.example.placemark_android.R
import timber.log.Timber

class PlacedMarkAdapter(
    private var marks: List<PlacemarkModel>,
    private val listener: OnMarkListener,
) : RecyclerView.Adapter<PlacedMarkAdapter.MarkViewHolder>() {

    class MarkViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val coordinatesText: TextView =
            itemView.findViewById(R.id.coordinatesText)

        val markImage: ImageView =
            itemView.findViewById(R.id.markImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_placed_mark,
                parent,
                false
            )

        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MarkViewHolder,
        position: Int
    ) {

        val mark = marks[position]

        val imageUri = mark.imageUri?.let { Uri.parse(it) }

        holder.titleText.text =
            "${mark.id}: ${mark.title}"

        holder.descriptionText.text =
            mark.desc

        holder.coordinatesText.text =
            "X: ${mark.x}, Y: ${mark.y}"

        holder.itemView.setOnClickListener {
            Timber.i("Mark clicked: id=%d, title=%s", mark.id, mark.title)
            listener.onMarkClick(mark)

        }

        holder.itemView.setOnLongClickListener {
            Timber.i("Mark long-clicked: id=%d, title=%s", mark.id, mark.title)
            listener.onMarkLongClick(mark)

        }

        if (imageUri != null) {
            holder.markImage.setImageURI(imageUri)
        } else {
            holder.markImage.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }
    }

    override fun getItemCount(): Int {
        return marks.size
    }

    fun updateMarks(newMarks: List<PlacemarkModel>) {
        Timber.d("Updating marks: %d -> %d items", marks.size, newMarks.size)
        marks = newMarks
        notifyDataSetChanged()
    }
}