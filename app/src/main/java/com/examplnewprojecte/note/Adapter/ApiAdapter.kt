package com.examplnewprojecte.note.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.examplnewprojecte.note.databinding.ItemApiBinding

class ApiAdapter(
    private val apiList: List<String>,
    private val onApiSelected: (String) -> Unit
) : RecyclerView.Adapter<ApiAdapter.ApiViewHolder>() {

    private var selectedPosition = -1 // Lưu vị trí API được chọn

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiViewHolder {
        val binding = ItemApiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApiViewHolder, position: Int) {
        holder.bind(apiList[position], position)
    }

    override fun getItemCount(): Int = apiList.size

    inner class ApiViewHolder(private val binding: ItemApiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(apiName: String, position: Int) {
            binding.apiNameText.text = apiName
            binding.apiRadioButton.isChecked = position == selectedPosition

            binding.apiRadioButton.setOnClickListener {
                if (selectedPosition != position) {
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onApiSelected(apiName)
                }
            }
        }
    }

    fun getSelectedApi(): String? {
        return if (selectedPosition != -1) apiList[selectedPosition] else null
    }
}