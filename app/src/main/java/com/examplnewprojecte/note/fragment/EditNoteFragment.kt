package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.ViewModel.NoteViewModel
import com.examplnewprojecte.note.databinding.FragmentEditNoteBinding

class EditNoteFragment : Fragment() {
    private lateinit var binding: FragmentEditNoteBinding
    private val noteViewModel: NoteViewModel by activityViewModels()
    private var noteId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            noteId = it.getInt("note_id", -1)
            val content = it.getString("note_content", "")
            binding.editText.setText(content)  // Gán nội dung vào EditText
        }

        // Xử lý nút lưu
        binding.saveButton.setOnClickListener {
            val noteContent = binding.editText.text.toString().trim()
            if (noteContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                if (noteId == -1) {
                    // Ghi chú mới
                    val newNote = NoteEntity(content = noteContent)
                    noteViewModel.insert(newNote)
                } else {
                    // Cập nhật ghi chú cũ
                    val updatedNote = NoteEntity(id = noteId, content = noteContent)
                    noteViewModel.update(updatedNote)
                }
                parentFragmentManager.popBackStack()
            }
        }


        // Xử lý khi nhấn nút Back
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Quay lại trang trước
        }
    }

    companion object {
        fun newInstance(noteId: Int, content: String) = EditNoteFragment().apply {
            arguments = Bundle().apply {
                putInt("note_id", noteId)
                putString("note_content", content)
            }
        }
    }
}