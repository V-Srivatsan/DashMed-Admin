package com.dashmed.admin.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.admin.R
import com.dashmed.admin.networking.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isCompleted
import kotlinx.coroutines.launch
import java.lang.Exception

class InventoryVM : ViewModel() {

    lateinit var uid: String
    lateinit var items: GetInventoryRes
    lateinit var res: EmptyRes

    fun getInventory(): Job {
        return viewModelScope.launch {
            try {
                items = API.service.getInventory(uid)
            } catch (e: Exception) {
                items = GetInventoryRes(false, null, listOf())
            }
        }
    }

    fun updateItem(item_id: String, date: String, quantity: Int): Job {
        return viewModelScope.launch {
            try {
                res = API.service.updateItem(UpdateItemReq(uid, item_id, date, quantity))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

    fun removeItem(item_id: String): Job {
        return viewModelScope.launch {
            try {
                res = API.service.removeItem(RemoveItemReq(uid, item_id))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

}