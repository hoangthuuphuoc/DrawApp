package com.example.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.data.model.CropItem
import com.example.draw.databinding.ItemCropBinding

class CropAdapter(
    private val listCrop: List<CropItem>, private val onClick: (CropItem) -> Unit
) : RecyclerView.Adapter<CropAdapter.CropRatioViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CropRatioViewHolder {
        val binding = ItemCropBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return CropRatioViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CropRatioViewHolder, position: Int
    ) {
        holder.bind(listCrop[position])
    }

    override fun getItemCount(): Int {
        return listCrop.size
    }

    inner class CropRatioViewHolder(
        private val binding: ItemCropBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            cropRatio: CropItem
        ) {

            binding.tvRatio.text = cropRatio.name

            binding.root.setOnClickListener {
                onClick(
                    cropRatio
                )
            }
        }
    }
}