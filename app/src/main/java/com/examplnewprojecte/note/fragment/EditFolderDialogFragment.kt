package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.examplnewprojecte.note.Entity.FolderEntity
import com.examplnewprojecte.note.ViewModel.FolderViewModel
import com.examplnewprojecte.note.databinding.DialogAddFolderBinding

class EditFolderDialogFragment(
    private val folder: FolderEntity, private val onFolderUpdated: (FolderEntity) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddFolderBinding? = null
    private val binding get() = _binding!!
    private val folderViewModel: FolderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Gán tên cũ vào EditText
        binding.folderNameEditText.setText(folder.name)

        binding.cancelButton.setOnClickListener { dismiss() }

        binding.addButton.text = "Cập nhật"
        binding.addButton.setOnClickListener {
            val newName = binding.folderNameEditText.text.toString().trim()
            when {
                newName.isEmpty() -> binding.folderNameInputLayout.error = "Tên không được để trống"
                newName.length > 50 -> binding.folderNameInputLayout.error = "Tên quá dài"

                else -> {
                    val updatedFolder = folder.copy(name = newName)
                    folderViewModel.update(updatedFolder)
                    onFolderUpdated(updatedFolder)
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
