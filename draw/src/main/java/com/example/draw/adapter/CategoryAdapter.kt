package com.example.draw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.draw.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val listCategory: List<String>, private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            category: String, position: Int
        ) {

            binding.tvCategory.text = category

            binding.tvCategory.isSelected = position == selectedPosition

            binding.tvCategory.setOnClickListener {

                val oldPosition = selectedPosition

                selectedPosition = bindingAdapterPosition

                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)

                onClick(category)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CategoryViewHolder {

        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder, position: Int
    ) {
        holder.bind(
            listCategory[position], position
        )
    }
}