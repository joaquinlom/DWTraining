package com.dwtraining.lom.listeners

import com.dwtraining.lom.models.DeletionLog

interface LogListener {
    fun onLogItemListener(log:DeletionLog)
}