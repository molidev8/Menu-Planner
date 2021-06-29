package es.uam.eps.tfg.menuPlanner.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.R.*
import es.uam.eps.tfg.menuPlanner.util.LoadingDialog

class MainMenuFragment: Fragment() {

    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var viewModel: MainMenuViewModel
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireNotNull(this.activity)
        viewModel = ViewModelProvider(this, MainMenuViewModel
            .FACTORY((requireContext().applicationContext as MenuApplication).repository, activity.application))
            .get(MainMenuViewModel::class.java)
        activity.findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.VISIBLE
        val preferences: SharedPreferences = activity.getSharedPreferences("pref", Context.MODE_PRIVATE)
        if (!preferences.getBoolean("isSaved", false)){
            loadingDialog = LoadingDialog(activity)
            loadingDialog.startLoadingDialog()
        }
    }

    private inner class CardHolder(view: View): RecyclerView.ViewHolder(view) {
        var day: String = ""
        private val cardText: TextView = view.findViewById(R.id.cardText)

        init {
            itemView.setOnClickListener {
                val action = MainMenuFragmentDirections.actionMainMenuFragmentToDayMenuFragment(day)
                findNavController().navigate(action)
            }
        }

        fun bind(day: String) {
            this.day = day
            cardText.text = day
        }
    }

    private inner class CardAdapter(): RecyclerView.Adapter<CardHolder>() {

        val days = listOf(
            getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday),
            getString(R.string.thursday), getString(R.string.friday), getString(R.string.saturday),
            getString(R.string.sunday))

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
            return CardHolder(layoutInflater.inflate(layout.list_item_menu, parent, false))
        }

        override fun getItemCount(): Int = days.size

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            holder.bind(days.get(position))
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layout.fragment_main_menu, container, false)
        cardRecyclerView = view?.findViewById(R.id.menu_recycler_view) as RecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.menuCreated.observe(viewLifecycleOwner, Observer { menuCreated ->
            loadingDialog.endLoadingDialog()
        })

        adapter = CardAdapter()
        cardRecyclerView.adapter = adapter

        return view
    }
}