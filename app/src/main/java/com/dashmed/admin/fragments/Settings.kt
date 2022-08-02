package com.dashmed.admin.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.dashmed.admin.LoginActivity
import com.dashmed.admin.MainActivity
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.databinding.FragmentSettingsBinding
import com.dashmed.admin.viewmodels.SettingsVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout


class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsVM
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this)[SettingsVM::class.java]
        sharedPreferences.getString("UID", null)?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)

        Utils.showDialog(R.layout.password_dialog, inflater, requireContext()).let { dialog ->
            val input: TextInputLayout = dialog.findViewById(R.id.password_input)
            val cancelBtn: Button = dialog.findViewById(R.id.password_cancel)
            val proceedBtn: Button = dialog.findViewById(R.id.password_ok)
            val progress: ProgressBar = dialog.findViewById(R.id.password_progress)

            cancelBtn.setOnClickListener {
                dialog.dismiss()
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.nav_employees
            }

            input.editText?.addTextChangedListener {
                proceedBtn.isEnabled = Utils.validateField(input, Utils.Companion.VALIDATION_TYPE.PASSWORD)
            }

            proceedBtn.setOnClickListener {
                cancelBtn.visibility = View.GONE
                proceedBtn.visibility = View.GONE
                progress.visibility = View.VISIBLE

                viewModel.checkPassword(input.editText?.text.toString()).invokeOnCompletion {
                    val res = viewModel.res
                    if (res.valid)
                        dialog.dismiss()
                    else {
                        cancelBtn.visibility = View.VISIBLE
                        proceedBtn.visibility = View.VISIBLE
                        progress.visibility = View.GONE
                        input.error = res.message ?: getString(R.string.network_error)
                    }
                }
            }
        }

        var password = ""
        binding.settingsPassword.editText?.addTextChangedListener {
            if (Utils.validateField(binding.settingsPassword, Utils.Companion.VALIDATION_TYPE.PASSWORD)) {
                password = it.toString()
                if (binding.settingsConfirmPassword.editText?.text.toString() != password) {
                    if (binding.settingsConfirmPassword.editText?.text.toString().isNotEmpty())
                        binding.settingsConfirmPassword.error = "The passwords do not match!"
                    binding.settingsUpdatePassword.isEnabled = false
                }
            }
            else
                binding.settingsUpdatePassword.isEnabled = false
        }

        binding.settingsConfirmPassword.editText?.addTextChangedListener {
            if (it.toString() != password) {
                binding.settingsConfirmPassword.error = "The passwords do not match!"
                binding.settingsUpdatePassword.isEnabled = false
            }
            else {
                binding.settingsConfirmPassword.isErrorEnabled = false
                binding.settingsUpdatePassword.isEnabled = true
            }
        }


        binding.settingsUpdatePassword.setOnClickListener {
            binding.settingsUpdatePassword.visibility = View.GONE
            binding.settingsProgress.visibility = View.VISIBLE
            viewModel.updatePassword(binding.settingsPassword.editText?.text.toString()).invokeOnCompletion {
                binding.settingsUpdatePassword.visibility = View.VISIBLE
                binding.settingsProgress.visibility = View.GONE

                val res = viewModel.res
                if (res.valid) {
                    binding.settingsPassword.editText?.setText("")
                    binding.settingsPassword.isErrorEnabled = false

                    binding.settingsConfirmPassword.editText?.setText("")
                    binding.settingsConfirmPassword.isErrorEnabled = false

                    Utils.showSnackbar(coordinatorLayout, "Password updated successfully!")
                } else
                    Utils.showSnackbar(coordinatorLayout, res.message)
            }
        }

        binding.settingsSignOut.setOnClickListener {
            with (sharedPreferences.edit()) {
                clear()
                apply()
            }
            requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.settingsPassword

        return binding.root
    }
}