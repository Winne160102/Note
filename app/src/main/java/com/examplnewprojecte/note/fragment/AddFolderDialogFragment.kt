package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.examplnewprojecte.note.ViewModel.FolderViewModel
import com.examplnewprojecte.note.databinding.DialogAddFolderBinding

class AddFolderDialogFragment : DialogFragment() {

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

        binding.folderNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.folderNameInputLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.addButton.setOnClickListener {
            val folderName = binding.folderNameEditText.text.toString().trim()
            when {
                folderName.isEmpty() -> {
                    binding.folderNameInputLayout.error = "Tên thư mục không được để trống"
                }

                folderName.length > 50 -> {
                    binding.folderNameInputLayout.error = "Tên thư mục quá dài (tối đa 50 ký tự)"
                }

                !folderName.matches(Regex("^[a-zA-Z0-9\\s]+$")) -> {
                    binding.folderNameInputLayout.error = "Chỉ dùng chữ cái, số và khoảng trắng"
                }

                else -> {
                    folderViewModel.parentFolders.value?.let { folders ->
                        if (folders.any { it.name.equals(folderName, ignoreCase = true) }) {
                            binding.folderNameInputLayout.error = "Tên thư mục đã tồn tại"
                            return@setOnClickListener
                        }
                    }
                    folderViewModel.insert(folderName)
                    Toast.makeText(
                        requireContext(), "Đã thêm thư mục $folderName", Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddFolderDialogFragment()
    }
}