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
    private var folderName: String = "Ghi chú"
    private val TAG = "NotesFragment"
    private lateinit var notesAdapter: GroupedNotesAdapter
    private var allNotes: List<NoteEntity> = emptyList() // Lưu trữ toàn bộ ghi chú

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

        binding.titleText.text = folderName
        Log.d(TAG, "onViewCreated: title set to $folderName")

        notesAdapter = GroupedNotesAdapter(
            emptyMap(),
            onItemClick = { note -> /* Không cần làm gì, đã xử lý trong adapter */ },
            onDeleteModeChanged = { isDeleteMode ->
                if (isDeleteMode) {
                    binding.deleteButton.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.VISIBLE
                } else {
                    binding.deleteButton.visibility = View.INVISIBLE
                    binding.cancelButton.visibility = View.INVISIBLE
                }
            },
            noteViewModel = noteViewModel
        )

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }

        // Quan sát danh sách ghi chú
        noteViewModel.getNotesByFolder(folderId.toInt()).observe(viewLifecycleOwner) { notes ->
            allNotes = notes ?: emptyList()
            Log.d(TAG, "Notes updated: $allNotes")
            filterNotes(binding.searchEditText.text.toString().trim().lowercase())
        }

        // Tìm kiếm theo tiêu đề ghi chú
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            Log.d(TAG, "Search query: $query")
            filterNotes(query)
        }

        // Tìm kiếm khi nhấn nút "Tìm kiếm" trên bàn phím
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim().lowercase()
                Log.d(TAG, "Search action triggered: $query")
                filterNotes(query)
                true
            } else {
                false
            }
        }

        // Ẩn bàn phím khi chạm ra ngoài
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive && binding.searchEditText.isFocused) {
                    binding.searchEditText.clearFocus()
                    imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                    Log.d(TAG, "Keyboard hidden due to touch outside")
                }
            }
            false
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.addNoteButton.setOnClickListener {
            val newNote = NoteEntity(content = "", folderId = folderId.toInt(), createdDate = System.currentTimeMillis())
            noteViewModel.insert(newNote)
        }

        binding.deleteButton.setOnClickListener {
            val notesToDelete = notesAdapter.getNotesToDelete()
            if (notesToDelete.isNotEmpty()) {
                val message = "Bạn có chắc chắn muốn xóa ${notesToDelete.size} ghi chú?"
                showDeleteConfirmation(message) {
                    noteViewModel.deleteNotes(notesToDelete)
                    notesAdapter.deleteSelectedNotes()
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            notesAdapter.exitDeleteMode()
        }
    }

    private fun filterNotes(query: String) {
        val filteredNotes = if (query.isNotEmpty()) {
            allNotes.filter { it.title.lowercase().contains(query) }
        } else {
            allNotes
        }

        if (filteredNotes.isEmpty()) {
            binding.notesRecyclerView.visibility = View.GONE
            binding.emptyNotesText.visibility = View.VISIBLE
        } else {
            binding.notesRecyclerView.visibility = View.VISIBLE
            binding.emptyNotesText.visibility = View.GONE
            val groupedNotes = noteViewModel.groupNotesByDate(filteredNotes)
            notesAdapter.updateGroupedNotes(groupedNotes)
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