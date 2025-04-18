package com.examplnewprojecte.note.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
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
    private var allFolders: List<FolderEntity> = emptyList()

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

        // Đảm bảo cloud_layout và search_layout hiển thị
        binding.cloudLayout.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.VISIBLE

        // Khởi tạo FolderAdapter
        folderAdapter = FolderAdapter(
            emptyList(),
            onFolderClick = { folder ->
                val fragment = NotesFragment.newInstance(folder.id, folder.name)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onEditClick = { folder ->
                val dialog = EditFolderDialogFragment(folder) { updatedFolder ->
                    // ViewModel đã xử lý update
                }
                dialog.show(parentFragmentManager, "EditFolderDialog")
            },
            onDeleteClick = { folder ->
                showDeleteConfirmation(folder)
            }
        )

        binding.folderRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = folderAdapter
            visibility = View.GONE
        }

        // Nhấn vào cloud_layout để toggle
        binding.cloudLayout.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastToggleTime > TOGGLE_DEBOUNCE_MS) {
                Log.d(TAG, "cloud_layout clicked, toggling")
                lastToggleTime = currentTime
                toggleFolderListVisibility()
            }
        }

        // Nút thêm thư mục
        binding.btnAddFolder.setOnClickListener {
            showAddFolderDialog()
        }

        // Tìm kiếm theo tên thư mục (thời gian thực)
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            Log.d(TAG, "Search query: $query")
            filterFolders(query)
        }

        // Tìm kiếm khi nhấn nút "Tìm kiếm" trên bàn phím
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim().lowercase()
                Log.d(TAG, "Search action triggered: $query")
                filterFolders(query)
                true
            } else {
                false
            }
        }

        // Quan sát danh sách thư mục
        folderViewModel.parentFolders.observe(viewLifecycleOwner) { folders ->
            allFolders = folders ?: emptyList()
            Log.d(TAG, "Folders updated: $allFolders")
            filterFolders(binding.searchEditText.text.toString().trim().lowercase())
            binding.toggleIcon.setImageResource(
                if (binding.folderRecyclerView.visibility == View.VISIBLE) R.drawable.ic_arrow_down
                else R.drawable.ic_arrow_right
            )
            binding.cloudLayout.visibility = View.VISIBLE
        }

        // Xử lý nút "Ghi chú"
        binding.noteIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotesFragment())
                .addToBackStack(null)
                .commit()
        }

        // Xử lý nút "Được chia sẻ"
        binding.sharedLayout.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SharedFragment())
                .addToBackStack(null)
                .commit()
        }

        // Ẩn bàn phím khi chạm ra ngoài
        binding.contentLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive && binding.searchEditText.isFocused) {
                    binding.searchEditText.clearFocus()
                    imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                    Log.d(TAG, "Keyboard hidden due to touch outside")
                }
            }
            false // Cho phép các sự kiện chạm khác tiếp tục xử lý
        }
    }

    private fun filterFolders(query: String) {
        val filteredFolders = if (query.isNotEmpty()) {
            allFolders.filter { it.name.lowercase().contains(query) }
        } else {
            allFolders
        }
        folderAdapter.updateFolders(filteredFolders)
        binding.folderCount.text = filteredFolders.size.toString()
        Log.d(TAG, "Filtered folders: $filteredFolders")
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

    private fun showDeleteConfirmation(folder: FolderEntity) {
        val message = "Bạn có chắc chắn muốn xóa thư mục '${folder.name}'?"
        val dialog = ConfirmDeleteDialogFragment(message) {
            folderViewModel.delete(folder)
        }
        dialog.show(parentFragmentManager, "ConfirmDeleteDialog")
    }
}