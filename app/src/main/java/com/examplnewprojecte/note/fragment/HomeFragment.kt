package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplnewprojecte.note.Adapter.FolderAdapter
import com.examplnewprojecte.note.Entity.FolderEntity
import com.examplnewprojecte.note.R
import com.examplnewprojecte.note.ViewModel.FolderViewModel
import com.examplnewprojecte.note.databinding.FragmentHomeBinding
import com.examplnewprojecte.note.dialog.ConfirmDeleteDialogFragment

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val folderViewModel: FolderViewModel by activityViewModels()
    private lateinit var folderAdapter: FolderAdapter
    private val TAG = "HomeFragment"
    private var lastToggleTime = 0L
    private val TOGGLE_DEBOUNCE_MS = 300L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        try {
            binding = FragmentHomeBinding.inflate(inflater, container, false)
        } catch (e: Exception) {
            throw e
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Đảm bảo cloud_layout hiển thị
        binding.cloudLayout.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.VISIBLE

        // Khởi tạo FolderAdapter
        folderAdapter = FolderAdapter(emptyList(), onFolderClick = { folder ->
            val fragment = NotesFragment.newInstance(folder.id, folder.name)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit()
        }, onEditClick = { folder ->
            val dialog = EditFolderDialogFragment(folder) { updatedFolder ->
                // ViewModel đã xử lý update
            }
            dialog.show(parentFragmentManager, "EditFolderDialog")
        }, onDeleteClick = { folder ->
            showDeleteConfirmation(folder)
        })

        binding.folderRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = folderAdapter
            visibility = View.GONE
        }

        // Nhấn vào cloud_layout để toggle
        binding.cloudLayout.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            currentTime - lastToggleTime > TOGGLE_DEBOUNCE_MS
            Log.d(TAG, "cloud_layout clicked, toggling")
            lastToggleTime = currentTime
            toggleFolderListVisibility()

        }

        binding.btnAddFolder.setOnClickListener {
            showAddFolderDialog()
        }

        // Quan sát danh sách thư mục
        folderViewModel.parentFolders.observe(viewLifecycleOwner) { folders ->
            folderAdapter.updateFolders(folders ?: emptyList())
            binding.folderCount.text = (folders?.size ?: 0).toString()
            binding.toggleIcon.setImageResource(
                if (binding.folderRecyclerView.visibility == View.VISIBLE) R.drawable.ic_arrow_down
                else R.drawable.ic_arrow_right
            )
            binding.cloudLayout.visibility = View.VISIBLE
        }
    }

    private fun toggleFolderListVisibility() {
        binding.folderRecyclerView.visibility =
            if (binding.folderRecyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.toggleIcon.setImageResource(
            if (binding.folderRecyclerView.visibility == View.VISIBLE) R.drawable.ic_arrow_down
            else R.drawable.ic_arrow_right
        )
        binding.cloudLayout.visibility = View.VISIBLE
    }

    private fun showAddFolderDialog() {
        val dialog = AddFolderDialogFragment.newInstance()
        dialog.show(parentFragmentManager, "AddFolderDialog")
    }

    override fun onStart() {
        super.onStart()
    }

    // Trong HomeFragment
    private fun showDeleteConfirmation(folder: FolderEntity) {
        val message = "Bạn có chắc chắn muốn xóa thư mục '${folder.name}'?"
        val dialog = ConfirmDeleteDialogFragment(message) {
            folderViewModel.delete(folder)
        }
        dialog.show(parentFragmentManager, "ConfirmDeleteDialog")
    }
}