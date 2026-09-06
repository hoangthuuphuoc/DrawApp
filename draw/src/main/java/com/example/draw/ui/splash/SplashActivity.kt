package com.example.draw.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.draw.databinding.ActivitySplashBinding
import com.example.draw.ui.FragmentActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
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
        nextPage()
        delay()
    }

    fun delay(){
        viewModel.delay()
    }
    fun nextPage() {
        lifecycleScope.launch {
            viewModel.loading.collect {
                if (it) {
                    val intent = Intent(this@SplashActivity, FragmentActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }
    }
}
