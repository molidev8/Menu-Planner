package es.uam.eps.tfg.menuPlanner.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.Recipe

@BindingAdapter("readyInMinutes")
fun TextView.setReadyInMinutesFormatted(item: Recipe) {
    text = getResources().getString(R.string.readyInMinutes, item.readyInMinutes)
}

@BindingAdapter("title")
fun TextView.setTitleFormatted(item: Recipe) {
    text = item.title.capitalizeTitle()
}

@BindingAdapter("image")
fun ImageView.setImage(item: Recipe) {
    val imgUri = item.image.toUri().buildUpon().scheme("https").build()
    Glide.with(context).load(imgUri).apply(RequestOptions()
        .error(R.drawable.ic_outline_broken_image_24))
        .into(this)
}