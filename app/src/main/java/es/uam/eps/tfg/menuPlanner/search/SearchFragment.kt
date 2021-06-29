package es.uam.eps.tfg.menuPlanner.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.Recipe
import es.uam.eps.tfg.menuPlanner.databinding.FragmentSearchBinding
import es.uam.eps.tfg.menuPlanner.util.LoadingDialog
import es.uam.eps.tfg.menuPlanner.util.RecipeListAdapter
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireNotNull(this.activity)
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.FACTORY((requireContext().applicationContext as MenuApplication).repository)
        )
            .get(SearchViewModel::class.java)
        loadingDialog = LoadingDialog(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("Search", "$query introduced")
                searchView.clearFocus()
                viewModel.searchRecipes(query)
                loadingDialog.startLoadingDialog()
                return true
            }
        })

        val adapter = RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe: Recipe ->
            val action = SearchFragmentDirections.actionSearchFragmentToRecipeFragment(recipe.id_recipe)
            findNavController().navigate(action)
        })
        binding.menuRecyclerView.adapter = adapter

        binding.setLifecycleOwner(this)

        viewModel.searchResults.observe(viewLifecycleOwner, Observer { recipes ->
            Log.d("Search", "Results $recipes")
            loadingDialog.endLoadingDialog()
            adapter.submitList(recipes)
        })
        
        return binding.root
    }
}