package com.examplnewprojecte.note.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.examplnewprojecte.note.Entity.FolderEntity
import com.examplnewprojecte.note.databinding.ItemFolderBinding

class FolderAdapter(
    private var folders: List<FolderEntity>,
    private val onFolderClick: (FolderEntity) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(private val binding: ItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: FolderEntity) {
            binding.folderNameText.text = folder.name
            binding.root.setOnClickListener { onFolderClick(folder) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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