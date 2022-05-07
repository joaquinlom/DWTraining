package com.dwtraining.lom.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dwtraining.lom.R
import com.dwtraining.lom.listeners.LogListener
import com.dwtraining.lom.models.DeletionLog
import kotlinx.android.synthetic.main.fragment_detail_logs_fragment.*

class DetailDeletionLogsFragment: Fragment() ,LogListener{

    private var selectedLog:DeletionLog? = null
    private var isTablet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedLog = activity?.intent?.getParcelableExtra("log")
        isTablet = selectedLog == null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_logs_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (selectedLog != null) updateUI()
    }
    private fun updateUI(){
        Log.d("DetailLog", "Updating UI LOG")
        if (!isTablet) activity?.title = selectedLog?.name
        if (selectedLog?.date?.isNotEmpty() == true) {
            logDate.text = selectedLog?.date
        }
        val status = if (selectedLog?.status == 0) getString(R.string.detail_fugitive_in_jail)
        else getString(R.string.detail_fugitive_still_not_catch)
        logStatus.text = status


    }

    override fun onLogItemListener(log: DeletionLog) {
        selectedLog = log
        updateUI()
    }


}