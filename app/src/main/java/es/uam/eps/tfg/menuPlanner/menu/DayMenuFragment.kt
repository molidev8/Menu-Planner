package es.uam.eps.tfg.menuPlanner.menu

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


class DayMenuFragment: Fragment() {

    private lateinit var viewModel: DayMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireNotNull(this.activity)
        arguments?.let {
            val day = DayMenuFragmentArgs.fromBundle(it).day
            viewModel = ViewModelProvider(
                this, DayMenuViewModel.FACTORY(
                    (requireContext().applicationContext as MenuApplication).repository
                    , day
                )
            )
                .get(DayMenuViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMainMenuBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_menu, container, false)
        binding.viewModel = viewModel

        val adapter = RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe: Recipe ->
            val action = DayMenuFragmentDirections.actionDayMenuFragmentToComplexRecipeFragment(recipe.id_recipe)
            findNavController().navigate(action)
        })
        binding.menuRecyclerView.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            Log.d("Menu","Las recetas de hoy son $recipes")
            adapter.submitList(recipes)
        })

        binding.lifecycleOwner = this

        return binding.root
    }
}