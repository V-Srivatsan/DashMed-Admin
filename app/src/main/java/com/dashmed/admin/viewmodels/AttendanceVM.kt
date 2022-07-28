package com.dashmed.admin.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.admin.R
import com.dashmed.admin.networking.API
import com.dashmed.admin.networking.AddEntryReq
import com.dashmed.admin.networking.EmptyRes
import com.dashmed.admin.networking.GetAttendanceRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class AttendanceVM : ViewModel() {

    lateinit var uid: String
    lateinit var attRes: GetAttendanceRes
    lateinit var res: EmptyRes

    fun getAttendance(date: Int, month: Int, year: Int) : Job {
        return viewModelScope.launch {
            try {
                attRes = API.service.getAttendance(uid, "$year-$month-$date")
            } catch (e: Exception) {
                attRes = GetAttendanceRes(false, null, null)
            }
        }
    }

    fun addEntries(uids: List<String>, year: Int, month: Int, date: Int): Job {
        return viewModelScope.launch {
            try {
                res = API.service.addEntry(AddEntryReq(uid, uids, "$year-$month-$date"))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }
}