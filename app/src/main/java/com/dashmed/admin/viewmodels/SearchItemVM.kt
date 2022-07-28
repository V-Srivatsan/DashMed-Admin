package com.dashmed.admin.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.admin.networking.API
import com.dashmed.admin.networking.AddItemReq
import com.dashmed.admin.networking.EmptyRes
import com.dashmed.admin.networking.GetMedicinesRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchItemVM : ViewModel() {

    lateinit var medRes: GetMedicinesRes
    lateinit var res: EmptyRes
    lateinit var uid: String
    var searchJob: Job? = null


    fun search(query: String): Job? {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            try {
                medRes = API.service.getMedicines(query)
            } catch (e: Exception) {
                medRes = GetMedicinesRes(listOf())
            }
        }
        return searchJob
    }

    fun addItem(med_id: String, year: Int, month: Int, date: Int, quantity: Int): Job {
        return viewModelScope.launch {
            try {
                res = API.service.addItem(AddItemReq(uid, med_id, "$year-$month-$date", quantity))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }
}