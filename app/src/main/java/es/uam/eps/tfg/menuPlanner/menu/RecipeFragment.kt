package es.uam.eps.tfg.menuPlanner.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.Recipe
import es.uam.eps.tfg.menuPlanner.util.capitalizeTitle
import kotlinx.android.synthetic.main.fragment_recipe.*


class RecipeFragment : Fragment() {

    private lateinit var viewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireNotNull(this.activity)
        arguments?.let {
            val id = RecipeFragmentArgs.fromBundle(it).id
            viewModel = ViewModelProvider(this, RecipeViewModel.FACTORY(
                    (requireContext().applicationContext as MenuApplication).repository, id))
                        .get(RecipeViewModel::class.java)
        }
        arguments?.getInt(ARG_RECIPE_ID)?.let {
            viewModel = ViewModelProvider(this, RecipeViewModel.FACTORY(
                (requireContext().applicationContext as MenuApplication).repository, it))
                .get(RecipeViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    private fun updateUI(recipe: Recipe){
        recipe_title.text = recipe.title.capitalizeTitle()
        Glide.with(this@RecipeFragment).load(recipe.image).into(recipe_image)
        ready_in_minutes.text = getString(R.string.readyInMinutes, recipe.readyInMinutes)
        servings.text = getString(R.string.servings, recipe.servings)
        instructions.text = recipe.instructions
        sourceUrl.text = getString(R.string.sourceUrl, recipe.sourceUrl)
        rating_bar.rating = recipe.rating.toFloat()

        save_recipe_button.text = when (recipe.isSaved) {
            false -> getString(R.string.button_save)
            else -> getString(R.string.button_unsave)
        }

        save_recipe_button.setOnClickListener {
            if (!recipe.isSaved == false) {
                viewModel.saveRating(recipe, 0)
            }else {
                viewModel.saveRating(recipe, 5)
            }

            viewModel.saveRecipe(recipe, !recipe.isSaved)
        }

        rating_bar.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                viewModel.saveRating(recipe, rating.toInt())
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recipe.observe(viewLifecycleOwner, Observer { recipe ->
            updateUI(recipe)
        })
    }

    companion object Factory {
        private const val ARG_RECIPE_ID = "id"

        fun newInstance(id: Int): RecipeFragment {
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, id)
            return RecipeFragment().apply {
                arguments = args
            }
        }
    }
}
