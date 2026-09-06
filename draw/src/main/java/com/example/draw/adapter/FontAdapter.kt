package com.example.draw.sticker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.databinding.ItemFontBinding
import com.example.draw.sticker.model.FontItem

class FontAdapter(
    private val listFont: List<FontItem>,
    private val onClick: (FontItem) -> Unit
) : RecyclerView.Adapter<FontAdapter.FontViewHolder>() {

    inner class FontViewHolder(
        private val binding: ItemFontBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            fontItem: FontItem
        ) {

            binding.tvFontName.text =
                fontItem.name

            binding.tvFontPreview.typeface =
                ResourcesCompat.getFont(
                    binding.root.context,
                    fontItem.font
                )

            binding.root.setOnClickListener {
                onClick(
                    fontItem
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FontViewHolder {

        val binding =
            ItemFontBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return FontViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: FontViewHolder,
        position: Int
    ) {

        holder.bind(
            listFont[position]
        )
    }

    override fun getItemCount(): Int {
        return listFont.size
    }
}