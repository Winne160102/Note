package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplnewprojecte.note.Adapter.FolderAdapter
import com.examplnewprojecte.note.Entity.FolderEntity
import com.examplnewprojecte.note.R
import com.examplnewprojecte.note.ViewModel.FolderViewModel
import com.examplnewprojecte.note.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val folderViewModel: FolderViewModel by activityViewModels()
    private lateinit var folderAdapter: FolderAdapter
    private val TAG = "HomeFragment"
    private var lastToggleTime = 0L
    private val TOGGLE_DEBOUNCE_MS = 300L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            binding = FragmentHomeBinding.inflate(inflater, container, false)
            Log.d(TAG, "onCreateView: Binding inflated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: Failed to inflate binding", e)
            throw e
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Đảm bảo cloud_layout hiển thị
        binding.cloudLayout.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.VISIBLE
        Log.d(TAG, "onViewCreated: cloud_layout isShown=${binding.cloudLayout.isShown}, search_layout isShown=${binding.searchLayout.isShown}")

        // Khởi tạo FolderAdapter
        folderAdapter = FolderAdapter(emptyList()) { folder ->
            Log.d(TAG, "Folder clicked: ${folder.name}, id=${folder.id}")
            val fragment = NotesFragment.newInstance(folder.id, folder.name)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.folderRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = folderAdapter
            visibility = View.GONE // Ban đầu ẩn
            Log.d(TAG, "folderRecyclerView initialized, visibility=GONE")
        }

        // Nhấn vào cloud_layout để toggle
        binding.cloudLayout.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastToggleTime > TOGGLE_DEBOUNCE_MS) {
                Log.d(TAG, "cloud_layout clicked, toggling")
                lastToggleTime = currentTime
                toggleFolderListVisibility()
            } else {
                Log.d(TAG, "cloud_layout click ignored (debounce)")
            }
        }

        binding.btnAddFolder.setOnClickListener {
            Log.d(TAG, "btnAddFolder clicked")
            showAddFolderDialog()
        }

        // Quan sát danh sách thư mục
        folderViewModel.parentFolders.observe(viewLifecycleOwner) { folders ->
            Log.d(TAG, "parentFolders updated: ${folders?.size ?: 0} folders, data=$folders")
            folderAdapter.updateFolders(folders ?: emptyList())
            binding.folderCount.text = (folders?.size ?: 0).toString()
            binding.toggleIcon.setImageResource(
                if (binding.folderRecyclerView.visibility == View.VISIBLE)
                    R.drawable.ic_arrow_down
                else
                    R.drawable.ic_arrow_right
            )
            binding.cloudLayout.visibility = View.VISIBLE
            Log.d(TAG, "cloud_layout set to VISIBLE after folders update, folder_count=${binding.folderCount.text}")
        }
    }

    private fun toggleFolderListVisibility() {
        binding.folderRecyclerView.visibility =
            if (binding.folderRecyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.toggleIcon.setImageResource(
            if (binding.folderRecyclerView.visibility == View.VISIBLE)
                R.drawable.ic_arrow_down
            else
                R.drawable.ic_arrow_right
        )
        binding.cloudLayout.visibility = View.VISIBLE
        Log.d(TAG, "toggleFolderListVisibility: folderRecyclerView visibility=${binding.folderRecyclerView.visibility}, cloud_layout isShown=${binding.cloudLayout.isShown}, search_layout isShown=${binding.searchLayout.isShown}")
    }

    private fun showAddFolderDialog() {
        val dialog = AddFolderDialogFragment.newInstance()
        dialog.show(parentFragmentManager, "AddFolderDialog")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: cloud_layout isShown=${binding.cloudLayout.isShown}, search_layout isShown=${binding.searchLayout.isShown}, content_layout height=${binding.contentLayout.height}")
    }
}