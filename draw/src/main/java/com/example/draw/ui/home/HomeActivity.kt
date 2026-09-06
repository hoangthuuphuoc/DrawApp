package com.example.draw.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.draw.adapter.CategoryAdapter
import com.example.draw.adapter.ImageAdapter
import com.example.draw.data.local.ImagePreferencesManager
import com.example.draw.databinding.ActivityHomeBinding
import com.example.draw.sticker.ui.StickerActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private lateinit var imageAdapter: ImageAdapter

    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var dataStoreManager:
            ImagePreferencesManager

    private val viewModel:
            HomeViewModel by viewModels()

    private val listCategory = listOf(
        "All",
        "Favourite"
    )

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        dataStoreManager =
            ImagePreferencesManager(this)

        initView()
        observeData()
        observeDataStore()
        setOnListener()
    }
    private  fun setOnListener(){
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initView() {

        imageAdapter =
            ImageAdapter(
                emptyList()
            ) { imageItem ->

                val intent =
                    Intent(
                        this,
                        StickerActivity::class.java
                    )

                intent.putExtra(
                    "image",
                    imageItem.uri.toString()
                )

                startActivity(intent)
            }
        binding.rvGallery.layoutManager =
            GridLayoutManager(
                this,
                3
            )

        binding.rvGallery.adapter =
            imageAdapter

        categoryAdapter =
            CategoryAdapter(
                listCategory
            ) { category ->

                when (category) {

                    "All" -> {
                        lifecycleScope.launch {
                            dataStoreManager.saveFavourite(false)
                        }
                    }

                    "Favourite" -> {
                        lifecycleScope.launch {
                            dataStoreManager.saveFavourite(true)
                        }
                    }
                }
            }

        binding.rvCategory.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvCategory.adapter =
            categoryAdapter
    }

    private fun observeData() {

        lifecycleScope.launch {

            viewModel.listImage.collect { images ->

                imageAdapter.updateData(images)
            }
        }
    }

    private fun observeDataStore() {

        lifecycleScope.launch {

            dataStoreManager.getFavourite().collect { isFavourite ->

                    viewModel.loadAllImage(
                        contentResolver,
                        isFavourite
                    )
                }
        }
    }
}