package com.examplnewprojecte.note.Adapter

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.examplnewprojecte.note.Entity.FolderEntity
import com.examplnewprojecte.note.R
import com.examplnewprojecte.note.databinding.ItemFolderBinding

class FolderAdapter(
    private var folders: List<FolderEntity>,
    private val onFolderClick: (FolderEntity) -> Unit,
    private val onEditClick: (FolderEntity) -> Unit,
    private val onDeleteClick: (FolderEntity) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(private val binding: ItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: FolderEntity) {
            binding.folderNameText.text = folder.name

            binding.root.setOnClickListener {
                onFolderClick(folder)
            }

            binding.moreOptions.setOnClickListener { view ->
                showPopup(view, folder)
            }
        }

        private fun showPopup(view: View, folder: FolderEntity) {
            val popup = PopupMenu(view.context, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.item_folder_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditClick(folder)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(folder)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(folders[position])
    }

    override fun getItemCount(): Int = folders.size

    fun updateFolders(newFolders: List<FolderEntity>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
