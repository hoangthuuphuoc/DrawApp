package com.example.draw.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.databinding.ItemCreativeToolBinding
import com.example.draw.main.model.CreativeTool

class CreativeToolAdapter(
    private val items: List<CreativeTool>,
    private val onClick: (CreativeTool) -> Unit
) : RecyclerView.Adapter<CreativeToolAdapter.CreativeToolViewHolder>() {

    inner class CreativeToolViewHolder(
        private val binding: ItemCreativeToolBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CreativeTool) {
            binding.ivToolIcon.setImageResource(
                item.icon
            )

            binding.tvToolTitle.text =
                item.title

            binding.tvToolDescription.text =
                item.description

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreativeToolViewHolder {

        val binding = ItemCreativeToolBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CreativeToolViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: CreativeToolViewHolder,
        position: Int
    ) {
        holder.bind(
            items[position]
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}