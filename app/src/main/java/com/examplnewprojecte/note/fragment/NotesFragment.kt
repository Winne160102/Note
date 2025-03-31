package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplnewprojecte.note.R
import com.examplnewprojecte.note.ViewModel.NoteViewModel
import com.examplnewprojecte.note.adapters.GroupedNotesAdapter
import com.examplnewprojecte.note.databinding.FragmentNotesBinding
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.dialog.ConfirmDeleteDialogFragment

class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    private val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var groupedNotesAdapter: GroupedNotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupedNotesAdapter = GroupedNotesAdapter(
            emptyMap(),
            { note -> openEditNoteFragment(note) },
            { isSelectionMode -> toggleSelectionMode(isSelectionMode) }
        )

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecyclerView.adapter = groupedNotesAdapter

        noteViewModel.groupedNotes.observe(viewLifecycleOwner) { groupedNotes ->
            groupedNotesAdapter.updateGroupedNotes(groupedNotes)
        }

        binding.createNoteButton.setOnClickListener {
            openEditNoteFragment(null)
        }

        binding.deleteButton.setOnClickListener {
            ConfirmDeleteDialogFragment {
                val deletedNotes = groupedNotesAdapter.deleteSelectedNotes()
                if (deletedNotes.isNotEmpty()) {
                    noteViewModel.deleteNotes(deletedNotes)
                }
            }.show(parentFragmentManager, "ConfirmDeleteDialog")
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.cancelButton.setOnClickListener {
            exitSelectionMode()
        }
    }

    private fun openEditNoteFragment(note: NoteEntity?) {
        val fragment = EditNoteFragment.newInstance(note?.id ?: -1, note?.content ?: "")
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun toggleSelectionMode(isSelectionMode: Boolean) {
        binding.deleteButton.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        binding.cancelButton.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
    }

    private fun exitSelectionMode() {
        groupedNotesAdapter.exitSelectionMode()
        toggleSelectionMode(false)
    }
}
