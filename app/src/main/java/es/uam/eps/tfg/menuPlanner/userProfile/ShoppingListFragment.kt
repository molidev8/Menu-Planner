package es.uam.eps.tfg.menuPlanner.userProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.ShoppingList
import kotlinx.android.synthetic.main.list_item_shopping_list.view.*
import java.util.*

class ShoppingListFragment : Fragment() {

    private lateinit var adapter: ShoppingListFragment.CardAdapter
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ProfileViewModel.FACTORY(
            (requireContext().applicationContext as MenuApplication).repository
        ))
            .get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shopping_list, container, false)
        cardRecyclerView = view?.findViewById(R.id.shopping_list_layout) as RecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(activity)
        return view
    }

    private inner class CardHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.sp_day_textView)



        fun bind(shoppingList: List<ShoppingList>, day: String) {
            itemView.sp_elements_recycler.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = DayAdapter(shoppingList)
            }
            name.text = day
        }
    }

    private inner class CardAdapter(val shoppingList: List<ShoppingList>): RecyclerView.Adapter<ShoppingListFragment.CardHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListFragment.CardHolder {
            return CardHolder(layoutInflater.inflate(R.layout.list_item_shopping_list, parent, false))
        }

        override fun getItemCount(): Int = 7

        override fun onBindViewHolder(holder: ShoppingListFragment.CardHolder, position: Int) {
            when(position) {
                0 -> shoppingList.filter { it.id_day.contains(getString(R.string.monday).toLowerCase(
                    Locale.ROOT
                )
                )
                }.let {
                holder.bind(
                        it, getString(R.string.monday)
                    )
                }
                1 -> shoppingList.filter { it.id_day.contains(getString(R.string.tuesday).toLowerCase(
                    Locale.ROOT
                )
                )
                }.let {
                holder.bind(
                        it, getString(R.string.tuesday)
                    )
                }
                2 -> shoppingList.filter { it.id_day.contains(getString(R.string.monday).toLowerCase(
                    Locale.ROOT)) }.let {
                    holder.bind(
                        it, getString(R.string.wednesday)
                    )
                }
                3 -> shoppingList.filter { it.id_day.contains(getString(R.string.tuesday).toLowerCase(
                    Locale.ROOT
                )
                )
                }.let {
                holder.bind(
                        it, getString(R.string.thursday)
                    )
                }
                4 -> shoppingList.filter { it.id_day.contains(getString(R.string.friday).toLowerCase(
                    Locale.ROOT
                )
                )
                }.let {
                holder.bind(
                        it, getString(R.string.friday)
                    )
                }
                5 -> shoppingList.filter { it.id_day.contains(getString(R.string.saturday).toLowerCase(
                    Locale.ROOT
                )
                )
                }.let {
                holder.bind(
                        it, getString(R.string.saturday)
                    )
                }
                6 -> shoppingList.filter { it.id_day.contains(getString(R.string.friday).toLowerCase(
                    Locale.ROOT
                )
                )
                }.let {
                holder.bind(
                        it, getString(R.string.sunday)
                    )
                }
            }
        }
    }

    private inner class DayHolder(view: View): RecyclerView.ViewHolder(view) {
        lateinit var shoppingList: ShoppingList
        val check: CheckBox = view.findViewById(R.id.ingredient_checkbox)
        val name: TextView = view.findViewById(R.id.ingredient_name)
        val quantity: TextView = view.findViewById(R.id.ingredient_quantity)

        init {
            check.setOnClickListener {
                if (it is CheckBox)
                    shoppingList.checked = it.isChecked

                viewModel.updateShoppingList(shoppingList)
            }
        }

        fun bind(elem: ShoppingList) {
            shoppingList = elem
            check.isChecked = elem.checked
            name.text = elem.name.capitalize()
            quantity.text = elem.amount.toString().replace(".0", "") + " " + elem.unit
        }
    }

    private inner class DayAdapter(val shoppingList: List<ShoppingList>): RecyclerView.Adapter<ShoppingListFragment.DayHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
            return DayHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_ingredients_checkbox, parent, false))
        }

        override fun getItemCount(): Int = shoppingList.size

        override fun onBindViewHolder(holder: DayHolder, position: Int) {
            holder.bind(shoppingList[position])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.shoppingList.observe(viewLifecycleOwner, Observer { shoppingList ->
            adapter = CardAdapter(shoppingList)
            cardRecyclerView.adapter = adapter
            Log.d("Menu", "el usuario es $shoppingList")
        })
    }

    companion object Factory {
        fun newInstance() = ShoppingListFragment()
    }
}