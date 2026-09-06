package com.example.draw.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.draw.camera.ui.CameraActivity
import com.example.draw.data.reponsitory.MainRepository
import com.example.draw.databinding.ActivityHomeMainBinding
import com.example.draw.main.adapter.CreativeToolAdapter
import com.example.draw.main.adapter.InspirationAdapter
import com.example.draw.main.model.CreativeTool
import com.example.draw.ui.home.HomeActivity
import com.google.android.material.tabs.TabLayoutMediator

class HomeMainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeMainBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            binding.root
        )
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
            )

            insets
        }
        setupInspiration()
        setupCreativeTools()
        setupClicks()

    }

    private fun setupInspiration() {
        binding.vpInspiration.adapter = InspirationAdapter(
            MainRepository.inspirationItems
        )

        binding.vpInspiration.offscreenPageLimit = 1

        TabLayoutMediator(
            binding.tabInspiration, binding.vpInspiration
        ) { _, _ -> }.attach()
    }

    private fun setupCreativeTools() {
        binding.rvCreativeTools.apply {
            layoutManager = GridLayoutManager(
                this@HomeMainActivity, 2
            )

            adapter = CreativeToolAdapter(
                MainRepository.creativeTools
            ) { tool ->
                handleCreativeToolClick(
                    tool
                )
            }
        }
    }

    private fun setupClicks() {
        binding.cardEdit.setOnClickListener {
            openEdit()
        }

        binding.cardCamera.setOnClickListener {
            openCamera()
        }
    }

    private fun openEdit() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun handleCreativeToolClick(
        tool: CreativeTool
    ) {
        Toast.makeText(
            this, tool.title, Toast.LENGTH_SHORT
        ).show()
    }
}