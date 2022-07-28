package com.dashmed.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class Utils {
    companion object {

        enum class VALIDATION_TYPE {
            TEXT,
            PHONE,
            PASSWORD,
            NUMBER
        }

        fun validateField(input: TextInputLayout, type: VALIDATION_TYPE, field_name: String? = null): Boolean {
            when (type) {

                VALIDATION_TYPE.TEXT -> {
                    if (input.editText?.text.isNullOrEmpty()) {
                        input.error = "$field_name cannot be empty!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.PHONE -> {
                    if (!Patterns.PHONE.matcher(input.editText?.text.toString()).matches() || input.editText?.text.toString().length < 14) {
                        input.error = "Please provide a valid phone number!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.PASSWORD -> {
                    val regex = Regex(".{4,}[0-9]{1,}")

                    if (input.editText?.text.isNullOrEmpty()) {
                        input.error = "Password cannot be empty!"
                        return false
                    } else if (input.editText?.text.toString().length < 5) {
                        input.error = "Password is too short!"
                        return false
                    } else if (! input.editText?.text.toString().matches(regex)) {
                        input.error = "Password does not meet requirements!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.NUMBER -> {
                    val value = input.editText?.text.toString().toIntOrNull()
                    if (value == null) {
                        input.error = "$field_name cannot be empty!"
                        return false
                    } else if (value < 1) {
                        input.error = "$field_name cannot be less than 1!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

            }
        }

        fun showSnackbar (view: CoordinatorLayout, message: String?) {
            Snackbar.make(view, message ?: view.context.getString(R.string.network_error), Snackbar.LENGTH_SHORT).show()
        }

        fun showBottomSheet (layout: Int, inflater: LayoutInflater, context: Context): BottomSheetDialog {
            val view = inflater.inflate(layout, null)
            val dialog = BottomSheetDialog(context, R.style.SheetDialog)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun showDialog (prompt: Int, inflater: LayoutInflater, context: Context): AlertDialog {
            val view = inflater.inflate(prompt, null)
            val dialog = AlertDialog.Builder(context).setView(view).create()
            dialog.setCancelable(false)
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return dialog
        }

        fun getDateDiff(months: Int, date: Int, month: Int, year: Int): Long {
            var diffYear = year - ((months - (months % 12)) / 12)
            var diffMonth = (month + 1) - (months % 12)
            if (diffMonth < 0) {
                diffYear -= 1
                diffMonth += 12
            }
            val calendar = Calendar.getInstance()
            calendar.set(diffYear, diffMonth, date)
            return calendar.timeInMillis
        }

        fun getUID(activity: Activity): String? {
            return activity.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE).getString("UID", null)
        }

    }
}