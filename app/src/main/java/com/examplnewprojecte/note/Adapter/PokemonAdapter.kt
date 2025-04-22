package com.examplnewprojecte.note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.examplnewprojecte.note.databinding.ItemPokemonBinding

class PokemonAdapter(
    private var itemList: List<Triple<String, String, String>>,
    private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Triple<String, String, String>) {
            binding.pokemonName.text = item.first.replaceFirstChar { it.uppercase() }
            binding.itemType.text = if (item.third == "pokemon") "Pokémon" else "Biến hình"
            if (item.third == "transformation") {
                binding.characterName.text = "Nhân vật: ${item.second}"
                binding.characterName.visibility = View.VISIBLE
            } else {
                binding.characterName.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                onItemClick(item.first, item.third)
            }
        }
    }

    fun updatePokemonList(newList: List<Triple<String, String, String>>) {
        itemList = newList
        notifyDataSetChanged()
    }
}