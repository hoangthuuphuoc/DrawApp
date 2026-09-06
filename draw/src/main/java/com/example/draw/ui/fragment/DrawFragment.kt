package com.example.draw.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.draw.R
import com.example.draw.databinding.FragmentChungBinding
import com.example.draw.ui.FragmentActivity

class ChungFragment : Fragment(R.layout.fragment_chung) {

    private lateinit var binding: FragmentChungBinding

    companion object {

        private const val KEY_PAGE = "KEY_PAGE"

        fun newInstance(page: Int): ChungFragment {
            return ChungFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_PAGE, page)
                }
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    )
    {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChungBinding.bind(view)
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

        val currentItem = arguments?.getInt(KEY_PAGE) ?: 0

        val activity = requireActivity() as FragmentActivity

        when (currentItem) {
            0 -> {
                binding.ivBackground.setImageResource(R.drawable.img_fragement)
                binding.ivIcon.setImageResource(R.drawable.ic_gallery)
                binding.tvTitle.text = activity.title2
                binding.tvDescription.text = activity.description2
                binding.tvButtonFragment.visibility = View.GONE
            }

            1 -> {
                binding.ivBackground.setImageResource(R.drawable.img_fragment3)
                binding.ivIcon.setImageResource(R.drawable.ic_heart)
                binding.tvTitle.text = activity.title3
                binding.tvDescription.text = activity.description3
                binding.tvButtonFragment.visibility = View.GONE

            }

            2 -> {
                binding.ivBackground.setImageResource(R.drawable.img_fragment4)
                binding.ivIcon.setImageResource(R.drawable.ic_roket)
                binding.tvTitle.text = activity.title4
                binding.tvDescription.text = activity.description4
                binding.tvButtonFragment.visibility = View.VISIBLE


            }

        }

        binding.tvButtonFragment.setOnClickListener {
            activity.nextPage()
        }
    }
}