package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplnewprojecte.note.adapters.ApiAdapter
import com.examplnewprojecte.note.databinding.FragmentSharedBinding
import com.examplnewprojecte.note.R

class SharedFragment : Fragment() {
    private var _binding: FragmentSharedBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiAdapter: ApiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val apiList = listOf("PokemonAPI", "DragonBallAPI")
        apiAdapter = ApiAdapter(apiList) { apiName ->
            // Lưu API được chọn, không chuyển ngay
        }

        binding.apiRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = apiAdapter
        }

        binding.confirmButton.setOnClickListener {
            val selectedApi = apiAdapter.getSelectedApi()
            if (selectedApi != null) {
                val fragment = ApiFragment.newInstance(selectedApi)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                binding.titleText.text = "Vui lòng chọn một API"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}