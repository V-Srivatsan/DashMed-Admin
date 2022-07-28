package com.dashmed.admin.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.admin.R
import com.dashmed.admin.networking.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class EmployeesVM : ViewModel() {

    companion object {
        var employeeList = mutableListOf<Employee>()
    }

    lateinit var uid: String
    lateinit var employeeRes: GetEmployeeRes
    lateinit var res: EmptyRes
    lateinit var addRes: AddEmployeeRes

    fun getEmployees() : Job {
        return viewModelScope.launch {
            try {
                employeeRes = API.service.getEmployees(uid)
            } catch (e: Exception) {
                employeeRes = GetEmployeeRes(false, null, null)
            }
        }
    }

    fun addEmployee(username: String, password: String, name: String, phone: String) : Job {
        return viewModelScope.launch {
            try {
                addRes = API.service.addEmployee(AddEmployeeReq(uid, username, password, name, phone))
            } catch (e: Exception) {
                addRes = AddEmployeeRes(false, null, null)
            }
        }
    }

    fun updateEmployee(emp_id: String, emp_name: String, emp_phone: String) : Job {
        return viewModelScope.launch {
            try {
                res = API.service.updateEmployee(UpdateEmployeeReq(uid, emp_id, emp_name, emp_phone))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

    fun removeEmployee(emp_id: String) : Job {
        return viewModelScope.launch {
            try {
                res = API.service.removeEmployee(RemoveEmployeeReq(uid, emp_id))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

}