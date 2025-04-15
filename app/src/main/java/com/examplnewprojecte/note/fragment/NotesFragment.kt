package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.examplnewprojecte.note.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private var folderId: Long = -1
    private var folderName: String = "Ghi chú" // Mặc định nếu không có tên
    private val TAG = "NotesFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            folderId = it.getLong(ARG_FOLDER_ID, -1)
            folderName = it.getString(ARG_FOLDER_NAME, "Ghi chú")
        }
        Log.d(TAG, "onCreate: folderId=$folderId, folderName=$folderName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cập nhật tiêu đề
        binding.titleText.text = folderName
        Log.d(TAG, "onViewCreated: title set to $folderName")

        // TODO: Thêm logic hiển thị ghi chú theo folderId (nếu cần)
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
}