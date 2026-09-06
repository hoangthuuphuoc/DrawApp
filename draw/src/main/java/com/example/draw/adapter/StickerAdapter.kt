package com.example.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.R
import com.example.draw.data.model.StickerItem
import com.example.draw.databinding.ItemStickerBinding


class StickerAdapter(
    private var stickerList: List<StickerItem>,
    private val onClick: (StickerItem) -> Unit
) : RecyclerView.Adapter<StickerAdapter.StickerViewHolder>() {

    private var selectedPosition = -1

    inner class StickerViewHolder(
        private val binding: ItemStickerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StickerItem, position: Int) {

            binding.ivStickerItem.setImageResource(
                item.image
            )

            binding.flStickerItem.setBackgroundResource(
                if (position == selectedPosition) {
                   R.drawable.bg_sticker_item_selected
                } else {
                   R.drawable.bg_sticker_item
                }
            )

            binding.root.setOnClickListener {

                selectedPosition = position

                notifyDataSetChanged()

                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StickerViewHolder {

        val binding = ItemStickerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return StickerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StickerViewHolder,
        position: Int
    ) {
        holder.bind(
            stickerList[position],
            position
        )
    }

    override fun getItemCount(): Int {
        return stickerList.size
    }

    fun submitList(
        list: List<StickerItem>
    ) {
        stickerList = list
        selectedPosition = -1
        notifyDataSetChanged()
    }
}