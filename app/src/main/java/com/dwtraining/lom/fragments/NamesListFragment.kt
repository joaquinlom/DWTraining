package com.dwtraining.lom.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.dwtraining.lom.R
import com.dwtraining.lom.activities.DetailActivity
import com.dwtraining.lom.activities.HomeActivity
import com.dwtraining.lom.data.DatabaseBountyHunter
import com.dwtraining.lom.models.Fugitive
import kotlinx.android.synthetic.main.fragment_names_list.*

class NamesListFragment : Fragment() {

    private val database by lazy { DatabaseBountyHunter(requireContext())}
    // Se hace referencia al Fragment generado por XML en los Layouts y
    // se instancia en una View...
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_names_list, container, false)

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val status = arguments?.get(SECTION_NUMBER) as Int
        updateNames(namesListView, status)
        namesListView.setOnItemClickListener { _, _, position, _ -> // adapterView, view, position, id ->
            val fugitives = namesListView.tag as Array<Fugitive>
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra(SELECTED_FUGITIVE, fugitives[position])
            }
            (requireActivity() as HomeActivity).getActivityResult.launch(intent)
        }
    }

    private fun updateNames(listView: ListView?, status: Int) {
        val fugitives = database.obtainFugitives(status)
        if (fugitives.isNotEmpty()) {
            val values = ArrayList<String>().also { fugitives.mapTo(it) { fugitive -> fugitive.name } }
            listView?.apply {
                adapter = ArrayAdapter(requireContext(), R.layout.item_names_list, values)
                tag = fugitives
            }
        }
    }

    companion object {
        const val SECTION_NUMBER = "section_number"
        const val SELECTED_FUGITIVE = "selected_fugitive"
    }
}