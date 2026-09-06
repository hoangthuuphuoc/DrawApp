package com.example.draw.camera.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.camera.data.FaceFilterItem
import com.example.draw.databinding.ItemFaceFilterBinding


class FaceFilterAdapter(
    private val onClick: (FaceFilterItem) -> Unit
) : ListAdapter<FaceFilterItem, FaceFilterAdapter.FilterViewHolder>(
    DiffCallback
) {

    private var selected: Int? = null

    fun setSelected(
        resource: Int?
    ) {
        if (selected == resource) {
            return
        }

        selected = resource

        notifyItemRangeChanged(
            0, itemCount
        )
    }

    inner class FilterViewHolder(
        private val binding: ItemFaceFilterBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: FaceFilterItem
        ) {
            binding.ivFilter.setImageResource(
                    item.previewRes
                )

            val selectedNow = item.filterRes == selected

            binding.cardFilter.strokeWidth = if (selectedNow) {
                dp(2)
            } else {
                0
            }

            binding.cardFilter.strokeColor = Color.WHITE

            binding.root.setOnClickListener {
                    onClick(
                        item
                    )
                }
        }

        private fun dp(
            value: Int
        ): Int {
            return (value * binding.root.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FilterViewHolder {

        return FilterViewHolder(
            ItemFaceFilterBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
        )
    }

    override fun onBindViewHolder(
        holder: FilterViewHolder, position: Int
    ) {
        holder.bind(
            getItem(position)
        )
    }

    private object DiffCallback : DiffUtil.ItemCallback<FaceFilterItem>() {

        override fun areItemsTheSame(
            oldItem: FaceFilterItem, newItem: FaceFilterItem
        ): Boolean {
            return oldItem.previewRes == newItem.previewRes
        }

        override fun areContentsTheSame(
            oldItem: FaceFilterItem, newItem: FaceFilterItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}