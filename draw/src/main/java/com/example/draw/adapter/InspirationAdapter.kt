package com.example.draw.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.databinding.ItemInspirationBinding
import com.example.draw.main.model.InspirationItem

class InspirationAdapter(
    private val items: List<InspirationItem>
) : RecyclerView.Adapter<InspirationAdapter.InspirationViewHolder>() {

    inner class InspirationViewHolder(
        private val binding: ItemInspirationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InspirationItem) {
            binding.ivInspiration.setImageResource(
                item.image
            )

            binding.tvInspirationTitle.text = item.title

            binding.tvInspirationDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): InspirationViewHolder {

        val binding = ItemInspirationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return InspirationViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: InspirationViewHolder, position: Int
    ) {
        holder.bind(
            items[position]
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}