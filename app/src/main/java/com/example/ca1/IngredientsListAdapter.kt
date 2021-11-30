package com.example.ca1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.databinding.IngredientListItemBinding
import com.example.ca1.databinding.ListItemBinding
import com.example.ca1.model.Cocktail

class IngredientsListAdapter (
    private var ingredients: MutableMap<String, String>,
    private var listener: ListItemListener
):

    RecyclerView.Adapter<IngredientsListAdapter.ViewHolder>() {
//    var favourite: FavouriteEntity? = null
//    var isFavourite: Boolean = false
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = IngredientListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.ingredient_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = ingredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // will need some other method for getting single item from map
        //val ingredient = ingredients?.getValue(holder.adapterPosition)

        val list = ingredients.toList()

        val ingredient = list.get(holder.adapterPosition)

        //val list: List<Pair<String, String>> = ingredients.toList()



        // this 'with' block means we can refer to lots of stuff inside the binding
        with(holder.binding) {
            fun <K, V> printMap(map: Map<K, V>) {
                for ((key, value) in ingredients.entries) {
                    Log.i("Ingredient:", "$key, $value")
                    //ingredientText.text = key
                    //measureText.text = value

                    ingredientText.text = ingredient.first
                    measureText.text = ingredient.second
                }
            }

            print(ingredients.toList())

            printMap(ingredients)

        }
    }

    interface ListItemListener {

    }
}