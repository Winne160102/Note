package com.examplnewprojecte.note.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.examplnewprojecte.note.databinding.DialogConfirmDeleteBinding

class ConfirmDeleteDialogFragment(private val onConfirm: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogConfirmDeleteBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())

        dialog.setContentView(binding.root)
        dialog.setCancelable(true) // Cho phép đóng hộp thoại khi bấm ra ngoài

        // Khi nhấn "Xoá"
        binding.confirmButton.setOnClickListener {
            onConfirm.invoke() // Gọi callback để thực hiện xoá
            dismiss()
        }

        // Khi nhấn "Hủy"
        binding.cancelButton.setOnClickListener {
            dismiss() // Đóng hộp thoại
        }
        return dialog
    }
}
