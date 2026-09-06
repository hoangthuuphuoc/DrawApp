package com.example.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.databinding.ItemColorBinding

class ColorAdapter(
    private val colors: List<Int>,
    private val onColorClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorViewHolder {
        val binding = ItemColorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ColorViewHolder,
        position: Int
    ) {
        val color = colors[position]
        holder.binding.root.setCardBackgroundColor(color)
        holder.binding.root.setOnClickListener {
            onColorClick(color)
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    inner class ColorViewHolder(
        val binding: ItemColorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }
}