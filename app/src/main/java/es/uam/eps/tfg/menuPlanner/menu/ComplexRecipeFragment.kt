package es.uam.eps.tfg.menuPlanner.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import es.uam.eps.tfg.menuPlanner.R
import kotlinx.android.synthetic.main.fragment_recipe_complex.*

class ComplexRecipeFragment : Fragment() {

    private lateinit var tabsText: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabsText = listOf(getString(R.string.recipe_info),
            getString(R.string.ingredients), getString(
                R.string.learning_info
            ))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_complex, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val id = RecipeFragmentArgs.fromBundle(it).id
            pager_recipe.adapter = ViewPagerAdapter(id,
                childFragmentManager,
                lifecycle
            )
        }
        TabLayoutMediator(tabs, pager_recipe) { tab, position ->
            tab.text = tabsText.get(position)
        }.attach()
    }


}

class ViewPagerAdapter(val id: Int, manager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(manager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> RecipeFragment.newInstance(id)
        1 -> IngredientsFragment.newInstance(id)
        else -> LearnFragment.newInstance(id)
    }
}