package com.example.ca1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1.databinding.IngredientListItemBinding
import com.example.ca1.model.Ingredient
import com.squareup.picasso.Picasso

class IngredientsListAdapter (
    private var ingredients: MutableMap<String, String>,
    private var listener: ListItemListener,
):
    RecyclerView.Adapter<IngredientsListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = IngredientListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ingredient_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = ingredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = ingredients.toList()

        val ingredient = list.get(holder.adapterPosition)
        // this 'with' block means we can refer to lots of stuff inside the binding
        with(holder.binding) {
            fun <K, V> printMap(map: Map<K, V>) {
                for ((key, value) in ingredients.entries) {
                    Log.i("Ingredient:", "$key, $value")
                    //ingredientText.text = key
                    //measureText.text = value

                    // TODO: Finish onclick for ingredient
                    // Have api method, make a function for it in the viewmodel and call it in the fragment
                    // only if ingredient id not null
                    // then pass the result in here maybe?
                    // or pass it to the onclick implementation of the interface in the fragment

                    if(key.isNotEmpty() && value.isNotEmpty()) {
                        ingredientText.text = ingredient.second
                        // Sometimes null, eg 'sugar' might not really have a measure, just to taste
                        // They come in the form of strings literally with the word 'null' because I'm using optString in viewfragment's raw json parsing method to ensure a string is always returned
                        if(ingredient.first != "null") {measureText.text = ingredient.first}

                        // Each ingredient has an associated image, we pass this endpoint to the Picasso library, which loads an image from the URL
                        // Picasso handles errors for us by adding a placeholder
                        Picasso.get()
                            .load("https://www.thecocktaildb.com/images/ingredients/${ingredientText.text}.png")
                            .error(R.drawable.ic_launcher_background).resize(50, 50).centerInside()
                            .into(imageView2);

                        root.setOnClickListener{
                            // and this is the unique ID for that piece of data
                            if (ingredient != null) {
                                listener.onItemClicked(ingredient.second)
                            }
                        }
                    }
                }
            }
            print(ingredients.toList())
            printMap(ingredients)
        }
    }

    interface ListItemListener {
        fun onItemClicked(ingredientName: String)
    }
}