package com.example.ca1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.data.CocktailEntity
import com.example.ca1.databinding.ListItemBinding

class CocktailsListAdapter(
    private val cocktailsList: List<CocktailEntity>,
    // this listener object is a reference to the fragment that is calling the adapter
    private val listener: ListItemListener
) :
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
            // in here we already have a reference to the binding
            // so we get a reference to the root

            // we notify the listener class (the fragment) that the user has clicked on *this* row
            root.setOnClickListener{
                // and this is the unique ID for that piece of data
                listener.onItemClick(cocktail.id)
            }
        }
    }

    // handle a click on a list item
    // we will use the navigation component to display the cocktail view component then

    // set up a relationship between the data item being clicked and the fragment in which the data is displayed
    interface ListItemListener {
        // passing the current note ID
        fun onItemClick(cocktailId: Int)
    }
}