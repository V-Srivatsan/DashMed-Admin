package com.dashmed.admin.viewmodels

import android.content.Context
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.networking.API
import com.dashmed.admin.networking.LoginReq
import com.dashmed.admin.networking.LoginRes
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginVM : ViewModel() {

    lateinit var username: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit var res: LoginRes

    private var isListening = false

    private fun addListeners() {
        if (! isListening) {
            isListening = true
            username.editText?.addTextChangedListener {
                validateFields()
            }
            password.editText?.addTextChangedListener {
                validateFields()
            }
        }
    }

    fun validateFields(): Boolean {
        var valid = true
        addListeners()

        if (! Utils.validateField(username, Utils.Companion.VALIDATION_TYPE.TEXT, "Username"))
            valid = false

        if (! Utils.validateField(password, Utils.Companion.VALIDATION_TYPE.PASSWORD))
            valid = false

        return valid
    }

    fun login(): Job {
        return viewModelScope.launch {
            try {
                res = API.service.login(LoginReq(
                    username.editText?.text.toString(),
                    password.editText?.text.toString()
                ))
            } catch (e: Exception) {
                res = LoginRes(false, null, null)
            }
        }
    }

}