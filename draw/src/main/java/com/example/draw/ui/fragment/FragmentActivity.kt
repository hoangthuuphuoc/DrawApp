package com.example.draw.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.draw.R
import com.example.draw.adapter.FragmentAdapter
import com.example.draw.databinding.ActivityFragmentBinding
import com.example.draw.main.ui.HomeMainActivity

class FragmentActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFragmentBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: FragmentAdapter

    val title2 = "Discover Great Artists"
    val title3 = "Find Inspiration"
    val title4 = "Create Your Own Art"


    val description2 =
        "Discover talented artists and\nbeautiful works of art."

    val description3 =
        "Find inspiration through amazing\nartworks from around the world."

    val description4 =
        "Turn your inspiration into\nsomething beautiful."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = FragmentAdapter(this)

        binding.vpOnboarding.adapter=adapter
        binding.vpOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handleView(position)
            }
        })
    }

    fun nextPage() {

        val currentItem = binding.vpOnboarding.currentItem

        if (currentItem < adapter.itemCount - 1) {
            binding.vpOnboarding.setCurrentItem(currentItem + 1, true)
        } else {
            startActivity(
                Intent(
                    this,
                    HomeMainActivity::class.java
                )
            )
            finish()
        }
    }
    fun handleView(positon: Int){
        when(positon){
            0-> {
                binding.vIndicator2.setBackgroundResource(R.drawable.bg_indicator_normal)
                binding.vIndicator1.setBackgroundResource(R.drawable.bg_indicator_selected)
                binding.vIndicator3.setBackgroundResource(R.drawable.bg_indicator_normal)
            }
            1->{
                binding.vIndicator2.setBackgroundResource(R.drawable.bg_indicator_selected)
                binding.vIndicator1.setBackgroundResource(R.drawable.bg_indicator_normal)
                binding.vIndicator3.setBackgroundResource(R.drawable.bg_indicator_normal)
            }
            2->{
                binding.vIndicator3.setBackgroundResource(R.drawable.bg_indicator_selected)
                binding.vIndicator1.setBackgroundResource(R.drawable.bg_indicator_normal)
                binding.vIndicator2.setBackgroundResource(R.drawable.bg_indicator_normal)
            }
        }


    }
}