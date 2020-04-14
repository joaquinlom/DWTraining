package com.dwtraining.droidbountyhunter.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.dwtraining.droidbountyhunter.R
import com.dwtraining.droidbountyhunter.activities.DetailActivity
import com.dwtraining.droidbountyhunter.models.Fugitive
import kotlinx.android.synthetic.main.fragment_names_list.*

class NamesListFragment : Fragment() {

    // Se hace referencia al Fragment generado por XML en los Layouts y
    // se instancia en una View...
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_names_list, container, false)

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val status = 0
        updateNames(namesListView, status)
        namesListView.setOnItemClickListener { _, _, position, _ -> // adapterView, view, position, id ->
            val intent = Intent(context, DetailActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun updateNames(listView: ListView?, status: Int) {
        val fugitives = listOf(
            Fugitive(0, "Jonas García", 0),
            Fugitive(1, "Adriana Caballero", 0),
            Fugitive(2, "Susana González", 0),
            Fugitive(3, "Luis Ramírez", 0),
            Fugitive(4, "Armando Perez", 0)
        )
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
        private const val REQUEST_CODE = 0
    }
}