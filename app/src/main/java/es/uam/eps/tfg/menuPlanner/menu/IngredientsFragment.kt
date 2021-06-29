package es.uam.eps.tfg.menuPlanner.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.RecipeIngredients

class IngredientsFragment : Fragment() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: CardAdapter
    private lateinit var cardRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(ARG_RECIPE_ID)?.let {
            viewModel = ViewModelProvider(this, RecipeViewModel.FACTORY(
                (requireContext().applicationContext as MenuApplication).repository, it))
                .get(RecipeViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false)
        cardRecyclerView = view?.findViewById(R.id.ingredients_layout) as RecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(activity)
        return view
    }

    private inner class CardHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.ingredient_name)
        val quantity: TextView = view.findViewById(R.id.ingredient_quantity)

        fun bind(ingredient: RecipeIngredients) {
            name.text = ingredient.name.capitalize()
            quantity.text = ingredient.amount.toString().replace(".0", "") + " " + ingredient.unit
        }
    }

    private inner class CardAdapter(val ingredients: List<RecipeIngredients>): RecyclerView.Adapter<CardHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
            return CardHolder(layoutInflater.inflate(R.layout.list_item_ingredients, parent, false))
        }

        override fun getItemCount(): Int = ingredients.size

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            holder.bind(ingredients[position])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recipeIngredients.observe(viewLifecycleOwner, Observer { ing ->
            Log.d("Menu", "${ing}")
            adapter = CardAdapter(ing)
            cardRecyclerView.adapter = adapter
        })
    }



    companion object Factory {
        private const val ARG_RECIPE_ID = "id"

        fun newInstance(id: Int): IngredientsFragment {
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, id)
            return IngredientsFragment().apply {
                arguments = args
            }
        }
    }
}