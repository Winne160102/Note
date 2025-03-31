package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.examplnewprojecte.note.databinding.FragmentSharedBinding

class SharedFragment : Fragment() {
    private lateinit var binding: FragmentSharedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSharedBinding.inflate(inflater, container, false)

        // Xử lý sự kiện nút back
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}