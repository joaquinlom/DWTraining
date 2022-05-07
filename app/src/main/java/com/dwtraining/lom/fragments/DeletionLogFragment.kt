package com.dwtraining.lom.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dwtraining.lom.R
import com.dwtraining.lom.activities.DetailDeletionLogActivity
import com.dwtraining.lom.data.DatabaseBountyHunter
import com.dwtraining.lom.listeners.LogListener
import com.dwtraining.lom.models.DeletionLog
import kotlinx.android.synthetic.main.deletion_logs_fragment.*

class DeletionLogFragment:Fragment() {
    private val database by lazy { DatabaseBountyHunter(requireContext()) }
    private lateinit var adapter: ArrayAdapter<String>
    private var logs: Array<DeletionLog> = emptyArray()
    private var listener: LogListener? = null
    private var isTablet = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.deletion_logs_fragment,container,false)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if(activity?.supportFragmentManager?.findFragmentById(R.id.fragmentDetailDeletionLogs) != null)
                listener = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentDetailDeletionLogs) as DetailDeletionLogsFragment
        }else{
            listener = null
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logs = database.obtainDeletionLogs()
        val list = logs.takeIf { it.isNotEmpty() }?.map { "${it.name} --> ${it.date}" } ?: emptyList()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
        listLogs.adapter = adapter

        isTablet = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentDetailDeletionLogs) != null
        if (isTablet ) {
            listener = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentDetailDeletionLogs) as DetailDeletionLogsFragment
        }


        listLogs.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "$position - ${adapter.getItem(position)}",
                Toast.LENGTH_LONG).show()

            if(listener != null){
                listener?.onLogItemListener(logs[position])
            }else{
                val intent = Intent(requireContext(), DetailDeletionLogActivity::class.java).apply {
                    putExtra("log", logs[position])
                }
                startActivity(intent)
            }

        }

    }
}