package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.ViewModel.NoteViewModel
import com.examplnewprojecte.note.databinding.DialogEditNoteBinding
import com.examplnewprojecte.note.R

class EditNoteDialogFragment(
    private val note: NoteEntity?,
    private val folderId: Long,  // Đảm bảo tham số này được sử dụng
    private val onNoteUpdated: (NoteEntity) -> Unit
) : DialogFragment() {

    private var _binding: DialogEditNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)

        // Nếu có note cũ thì gán nội dung
        note?.let {
            binding.noteEditText.setText(it.content)
        }

        binding.cancelButton.setOnClickListener { dismiss() }

        binding.saveButton.setOnClickListener {
            val content = binding.noteEditText.text.toString().trim()
            if (content.isNotEmpty()) {
                if (note == null) {
                    // Khi tạo ghi chú mới, gán folderId
                    val newNote = NoteEntity(content = content, folderId = folderId.toInt())
                    noteViewModel.insert(newNote)
                    onNoteUpdated(newNote)
                } else {
                    val updatedNote = note.copy(content = content)
                    noteViewModel.update(updatedNote)
                    onNoteUpdated(updatedNote)
                }
                dismiss()
            } else {
                binding.noteInputLayout.error = "Nội dung không được để trống"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(note: NoteEntity?, folderId: Long, onNoteUpdated: (NoteEntity) -> Unit): EditNoteDialogFragment {
            return EditNoteDialogFragment(note, folderId, onNoteUpdated)
        }
    }
}