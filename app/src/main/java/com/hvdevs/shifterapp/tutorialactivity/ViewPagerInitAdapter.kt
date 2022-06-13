package com.hvdevs.shifterapp.tutorialactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hvdevs.shifterapp.R

class ViewPagerInitAdapter(private val images: List<Int>, private val title: List<String>): RecyclerView.Adapter<ViewPagerInitAdapter.ViewPagerViewHolder>() {
    class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.swipe_image, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val image = images[position]
        val title = title[position]
        holder.imageView.setImageResource(image)
        holder.textView.text = title
    }

    override fun getItemCount(): Int {
        return images.size
    }
}