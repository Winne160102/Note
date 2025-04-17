package com.examplnewprojecte.note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.R

class GroupedNotesAdapter(
    private var groupedNotes: Map<String, List<NoteEntity>>,
    private val onItemClick: (NoteEntity) -> Unit,
    private val onSelectionModeChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isSelectionMode = false
    private val selectedNotes = mutableSetOf<NoteEntity>()
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
            noteHolder.checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
            noteHolder.checkBox.isChecked = selectedNotes.contains(note)

            noteHolder.itemView.setOnClickListener {
                if (isSelectionMode) toggleSelection(note, position)
                else onItemClick(note)
            }

            noteHolder.itemView.setOnLongClickListener {
                enterSelectionMode(note)
                true
            }
        }
    }

    override fun getItemCount(): Int = flatList.size

    fun updateGroupedNotes(newGroupedNotes: Map<String, List<NoteEntity>>) {
        groupedNotes = newGroupedNotes
        flatList = generateFlatList()
        notifyDataSetChanged()
    }

    private fun enterSelectionMode(note: NoteEntity) {
        if (!isSelectionMode) {
            isSelectionMode = true
            selectedNotes.clear()
            selectedNotes.add(note)
            notifyDataSetChanged()
            onSelectionModeChanged(true) // Hiển thị nút Xóa/Hủy
        }
    }

    private fun toggleSelection(note: NoteEntity, position: Int) {
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note)
        } else {
            selectedNotes.add(note)
        }
        notifyItemChanged(position)
    }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedNotes.clear()
        notifyDataSetChanged()
        onSelectionModeChanged(false) // Ẩn nút Xóa/Hủy
    }

    // Phương thức mới: Lấy danh sách ghi chú đã chọn mà không xóa
    fun getSelectedNotes(): List<NoteEntity> {
        return selectedNotes.toList()
    }

    // Phương thức đã sửa: Chỉ xóa sau khi xác nhận
    fun deleteSelectedNotes() {
        groupedNotes = groupedNotes.mapValues { (_, notes) ->
            notes.filterNot { it in selectedNotes }
        }.filterValues { it.isNotEmpty() }

        flatList = generateFlatList()
        selectedNotes.clear()
        notifyDataSetChanged()
        exitSelectionMode()
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
        val content = view.findViewById<TextView>(R.id.noteContent)
        val checkBox: CheckBox = view.findViewById(R.id.noteCheckBox)

        fun bind(note: NoteEntity) {
            content.text = note.content
        }
    }
}