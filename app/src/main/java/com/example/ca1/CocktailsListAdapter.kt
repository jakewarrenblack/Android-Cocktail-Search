package com.example.ca1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.data.CocktailEntity
import com.example.ca1.databinding.ListItemBinding

class CocktailsListAdapter(private val cocktailsList: List<CocktailEntity>) :
    RecyclerView.Adapter<CocktailsListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = cocktailsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = cocktailsList[position]
        with(holder.binding) {
            cocktailText.text = cocktail.text
        }
    }
}