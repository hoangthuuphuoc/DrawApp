package com.example.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.draw.data.model.ImageItem
import com.example.draw.databinding.ItemGalleryBinding

class ImageAdapter(
    private var listImage: List<ImageItem>, private val onClick: (ImageItem) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(
        private val binding: ItemGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageItem) {

            Glide.with(binding.ivImage).load(item.uri).centerCrop().into(binding.ivImage)

            binding.ivImage.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ImageViewHolder {

        val binding = ItemGalleryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listImage.size
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder, position: Int
    ) {
        holder.bind(
            listImage[position]
        )
    }

    fun updateData(newList: List<ImageItem>) {
        listImage = newList
        notifyDataSetChanged()
    }
}