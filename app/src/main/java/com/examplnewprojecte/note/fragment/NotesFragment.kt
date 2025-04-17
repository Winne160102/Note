package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplnewprojecte.note.adapters.GroupedNotesAdapter
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.ViewModel.NoteViewModel
import com.examplnewprojecte.note.databinding.FragmentNotesBinding
import com.examplnewprojecte.note.dialog.ConfirmDeleteDialogFragment

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private var folderId: Long = -1
    private val noteViewModel: NoteViewModel by activityViewModels()
    private var folderName: String = "Ghi chú" // Mặc định nếu không có tên
    private val TAG = "NotesFragment"
    private lateinit var notesAdapter: GroupedNotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            folderId = it.getLong(ARG_FOLDER_ID, -1)
            folderName = it.getString(ARG_FOLDER_NAME, "Ghi chú")
        }
        Log.d(TAG, "onCreate: folderId=$folderId, folderName=$folderName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cập nhật tiêu đề
        binding.titleText.text = folderName
        Log.d(TAG, "onViewCreated: title set to $folderName")

        // Khởi tạo GroupedNotesAdapter
        notesAdapter = GroupedNotesAdapter(
            emptyMap(),
            onItemClick = { note ->
                // Xử lý khi click vào ghi chú, ví dụ: mở dialog chỉnh sửa
                val dialog = EditNoteDialogFragment.newInstance(note, folderId) { updatedNote ->
                    // Có thể cập nhật UI tại đây nếu cần
                }
                dialog.show(parentFragmentManager, "EditNoteDialog")
            },
            onSelectionModeChanged = { isSelectionMode ->
                // Hiển thị/ẩn nút xóa hoặc hủy
                if (isSelectionMode) {
                    binding.deleteButton.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.VISIBLE
                } else {
                    binding.deleteButton.visibility = View.INVISIBLE
                    binding.cancelButton.visibility = View.INVISIBLE
                }
            }
        )

        // Thiết lập RecyclerView
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }

        // Quan sát danh sách ghi chú từ ViewModel
        noteViewModel.getNotesByFolder(folderId.toInt()).observe(viewLifecycleOwner) { notes ->
            if (notes.isNullOrEmpty()) {
                binding.notesRecyclerView.visibility = View.GONE
                binding.emptyNotesText.visibility = View.VISIBLE
            } else {
                binding.notesRecyclerView.visibility = View.VISIBLE
                binding.emptyNotesText.visibility = View.GONE
                // Nhóm ghi chú theo ngày
                val groupedNotes = noteViewModel.groupNotesByDate(notes)
                if (groupedNotes.isEmpty()) {
                    binding.notesRecyclerView.visibility = View.GONE
                    binding.emptyNotesText.visibility = View.VISIBLE
                } else {
                    notesAdapter.updateGroupedNotes(groupedNotes)
                }
            }
        }

        // Xử lý khi nhấn nút Back
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Quay lại trang trước
        }

        // Xử lý nút thêm ghi chú
        binding.addNoteButton.setOnClickListener {
            val dialog = EditNoteDialogFragment.newInstance(null, folderId) { newNote ->
                // Không cần làm gì ở đây vì LiveData đã tự động cập nhật
            }
            dialog.show(parentFragmentManager, "EditNoteDialog")
        }

        // Xử lý nút xóa (khi ở chế độ chọn)
        binding.deleteButton.setOnClickListener {
            val selectedNotes = notesAdapter.getSelectedNotes() // Chỉ lấy danh sách, không xóa
            if (selectedNotes.isNotEmpty()) {
                val message = "Bạn có chắc chắn muốn xóa ${selectedNotes.size} ghi chú?"
                showDeleteConfirmation(message, {
                    noteViewModel.deleteNotes(selectedNotes) // Xóa trong cơ sở dữ liệu
                    notesAdapter.deleteSelectedNotes() // Xóa khỏi UI
                })
            }
        }

        // Xử lý nút hủy (thoát chế độ chọn)
        binding.cancelButton.setOnClickListener {
            notesAdapter.exitSelectionMode()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_FOLDER_ID = "folder_id"
        private const val ARG_FOLDER_NAME = "folder_name"

        fun newInstance(folderId: Long, folderName: String): NotesFragment {
            return NotesFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_FOLDER_ID, folderId)
                    putString(ARG_FOLDER_NAME, folderName)
                }
            }
        }
    }

    private fun showDeleteConfirmation(message: String, onConfirm: () -> Unit) {
        val dialog = ConfirmDeleteDialogFragment(message, onConfirm)
        dialog.show(parentFragmentManager, "ConfirmDeleteDialog")
    }
}