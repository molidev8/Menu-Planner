package es.uam.eps.tfg.menuPlanner.menu

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.Stats
import kotlinx.android.synthetic.main.fragment_learn.*
import kotlin.math.roundToInt

class LearnFragment : Fragment() {

    private lateinit var viewModel: RecipeViewModel

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
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    private fun pieChart(stats: Stats) {
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(stats.carbs/100*stats.calories, "Carbs"))
        entries.add(PieEntry(stats.protein/100*stats.calories, "Protein"))
        entries.add(PieEntry(stats.fat/100*stats.calories, "Fat"))

        val pieData = PieData(PieDataSet(entries, "Calories distribution").apply {
            colors = ColorTemplate.createColors(ColorTemplate.MATERIAL_COLORS)
            sliceSpace = 2f
            valueTextSize = 14f
        })

        cal_distribution_chart.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.SQUARE

        }

        cal_distribution_chart.apply {
            centerText = "Calories distribution"
            setEntryLabelColor(Color.WHITE);
            setTouchEnabled(false)
            animateY(1400, Easing.EaseInOutQuad);
            data = pieData
            description.isEnabled = false
            setDrawEntryLabels(false)
            invalidate()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recipe.observe(viewLifecycleOwner, Observer { recipe ->
            feature_textView.text = getString(R.string.feature_explained, recipe.cuisines.joinToString(), recipe.dishTypes)
            viewModel.stats.observe(viewLifecycleOwner, Observer { stats ->
                val serving = stats.first.calories.div(stats.second).times(100).roundToInt()
                val times: Float = when(recipe.dishTypes) {
                    "breakfast" -> ((BREAKFAST*100F) / serving)
                    "lunch" -> ((LUNCH*100F) / serving)
                    else -> ((DINNER*100F) / serving)
                }
                cal_quantity_textView.text = getString(R.string.calories, stats.first.calories.toString(), serving, times)
                pieChart(stats.first)
            })
        })
    }

    companion object Factory {
        private const val ARG_RECIPE_ID = "id"

        fun newInstance(id: Int): LearnFragment {
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, id)
            return LearnFragment().apply {
                arguments = args
            }
        }
    }
}