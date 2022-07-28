package com.dashmed.admin.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.admin.networking.API
import com.dashmed.admin.networking.EmptyRes
import com.dashmed.admin.networking.UpdatePassReq
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class SettingsVM : ViewModel() {

    lateinit var uid: String
    lateinit var res: EmptyRes

    fun checkPassword(password: String): Job {
        return viewModelScope.launch {
            try {
                res = API.service.checkPassword(uid, password)
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

    fun updatePassword(new_password: String): Job {
        return viewModelScope.launch {
            try {
                res = API.service.updatePassword(UpdatePassReq(uid, new_password))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }
}