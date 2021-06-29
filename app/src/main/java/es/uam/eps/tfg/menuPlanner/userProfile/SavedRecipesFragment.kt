package es.uam.eps.tfg.menuPlanner.userProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.Recipe
import es.uam.eps.tfg.menuPlanner.databinding.FragmentMainMenuBinding
import es.uam.eps.tfg.menuPlanner.util.RecipeListAdapter

class SavedRecipesFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this, ProfileViewModel.FACTORY(
                (requireContext().applicationContext as MenuApplication).repository
            )
        )
            .get(ProfileViewModel::class.java)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMainMenuBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_menu, container, false)
        binding.viewModel = viewModel

        val adapter = RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe: Recipe ->
            val action = ProfileFragmentDirections.actionProfileFragmentToRecipeFragment(recipe.id_recipe)
            findNavController().navigate(action)
        })
        binding.menuRecyclerView.adapter = adapter

        viewModel.savedRecipes.observe(viewLifecycleOwner, Observer { recipes ->
            Log.d("Menu","Las recetas de hoy son $recipes")
            adapter.submitList(recipes)
        })

        binding.setLifecycleOwner(this)

        return binding.root
    }


    companion object Factory {
        fun newInstance() = SavedRecipesFragment()
    }
}