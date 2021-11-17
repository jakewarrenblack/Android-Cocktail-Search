package com.example.ca1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.databinding.ListItemBinding
import com.example.ca1.model.Cocktail
// we use this library for parsing images
import com.bumptech.glide.Glide

class CocktailsListAdapter(
    private val cocktailsList: List<Cocktail>,
    // this listener object is a reference to the fragment that is calling the adapter
    private val listener: ListItemListener
) :
    // This is the ViewHolder definition,
    // which is extended off of our RecyclerView.
    // The ViewHolder then wraps around a View, which is managed by the RecyclerView
    // https://developer.android.com/guide/topics/ui/layout/recyclerview
    RecyclerView.Adapter<CocktailsListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
    }

    // We can see the implementation of our ViewHolder all below here
    // Whenever the RecyclerView is going to make a new ViewHolder,
    // it calls the onCreateViewHolder method.
    // This method creates and initialises our ViewHolder and the View associated with it - (the inner class above)
    // but it doesn't fill the view's contents as we have not yet bound it to any specific data.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // defining what layout xml item is responsible for the look of our list item
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = cocktailsList.size

    // This method fetches the appropriate data and fills in the view holder's layout using it
    // So in our case, we're finding the appropriate cocktail name in the list and displaying it in the list item's TextView widget.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = cocktailsList[position]
        // this 'with' block means we can refer to lots of stuff inside the binding
        with(holder.binding) {
            Glide.with(view.context).load(property.image).centerCrop().into(imageView)
            cocktailText.text = cocktail.strDrink
            // in here we already have a reference to the binding
            // so we get a reference to the root

            // we notify the listener class (the fragment) that the user has clicked on *this* row
            root.setOnClickListener{
                // and this is the unique ID for that piece of data
                listener.onItemClick(cocktail.idDrink, cocktail.strInstructions, cocktail.strDrink)
            }
        }
    }

    // handle a click on a list item
    // we will use the navigation component to display the cocktail view component then

    // set up a relationship between the data item being clicked and the fragment in which the data is displayed
    interface ListItemListener {
        // passing the current note ID
        fun onItemClick(cocktailId: Int, cocktailInstructions: String, cocktailName: String)
    }
}