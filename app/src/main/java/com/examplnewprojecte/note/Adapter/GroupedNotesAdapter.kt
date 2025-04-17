package com.examplnewprojecte.note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.R
import com.examplnewprojecte.note.ViewModel.NoteViewModel
import com.examplnewprojecte.note.fragment.NoteDetailFragment

class GroupedNotesAdapter(
    private var groupedNotes: Map<String, List<NoteEntity>>,
    private val onItemClick: (NoteEntity) -> Unit,
    private val onDeleteModeChanged: (Boolean) -> Unit,
    private val noteViewModel: NoteViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isDeleteMode = false
    private val notesToDelete = mutableSetOf<NoteEntity>()
    private var flatList: List<Any> = generateFlatList()

    override fun getItemViewType(position: Int): Int {
        return if (flatList[position] is String) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            GroupViewHolder(inflater.inflate(R.layout.item_group_header, parent, false))
        } else {
            NoteViewHolder(inflater.inflate(R.layout.item_note, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (flatList[position] is String) {
            (holder as GroupViewHolder).bind(flatList[position] as String)
        } else {
            val note = flatList[position] as NoteEntity
            val noteHolder = holder as NoteViewHolder

            noteHolder.bind(note)
            noteHolder.itemView.setOnClickListener {
                if (!isDeleteMode) {
                    val fragmentManager = (holder.itemView.context as FragmentActivity).supportFragmentManager
                    val noteDetailFragment = NoteDetailFragment.newInstance(note)
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, noteDetailFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    toggleDelete(note, position)
                }
            }

            noteHolder.itemView.setOnLongClickListener {
                if (!isDeleteMode) {
                    enterDeleteMode(note)
                }
                true
            }

            noteHolder.checkBox.visibility = if (isDeleteMode) View.VISIBLE else View.GONE
            noteHolder.checkBox.isChecked = notesToDelete.contains(note)
        }
    }

    override fun getItemCount(): Int = flatList.size

    fun updateGroupedNotes(newGroupedNotes: Map<String, List<NoteEntity>>) {
        groupedNotes = newGroupedNotes
        flatList = generateFlatList()
        notifyDataSetChanged()
    }

    private fun enterDeleteMode(note: NoteEntity) {
        isDeleteMode = true
        notesToDelete.clear()
        notesToDelete.add(note)
        notifyDataSetChanged()
        onDeleteModeChanged(true)
    }

    private fun toggleDelete(note: NoteEntity, position: Int) {
        if (notesToDelete.contains(note)) {
            notesToDelete.remove(note)
        } else {
            notesToDelete.add(note)
        }
        notifyItemChanged(position)
        if (notesToDelete.isEmpty()) {
            exitDeleteMode()
        }
    }

    fun exitDeleteMode() {
        isDeleteMode = false
        notesToDelete.clear()
        notifyDataSetChanged()
        onDeleteModeChanged(false)
    }

    fun deleteSelectedNotes() {
        groupedNotes = groupedNotes.mapValues { (_, notes) ->
            notes.filterNot { it in notesToDelete }
        }.filterValues { it.isNotEmpty() }

        flatList = generateFlatList()
        notesToDelete.clear()
        notifyDataSetChanged()
        exitDeleteMode()
    }

    fun getNotesToDelete(): List<NoteEntity> {
        return notesToDelete.toList()
    }

    private fun generateFlatList(): List<Any> {
        return groupedNotes.flatMap { (header, notes) -> listOf(header) + notes }
    }

    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.groupTitle)
        fun bind(titleText: String) {
            title.text = titleText
        }
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.noteText)
        val checkBox: CheckBox = view.findViewById(R.id.noteCheckBox)

        fun bind(note: NoteEntity) {
            content.text = if (note.title.isNotEmpty()) note.title else "Không có tiêu đề"
        }
    }
}