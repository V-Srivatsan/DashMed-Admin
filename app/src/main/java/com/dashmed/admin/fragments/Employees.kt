package com.dashmed.admin.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.adapters.EmployeesAdapter
import com.dashmed.admin.databinding.FragmentEmployeesBinding
import com.dashmed.admin.networking.Employee
import com.dashmed.admin.viewmodels.EmployeesVM
import com.google.android.material.textfield.TextInputLayout


class Employees : Fragment(), EmployeesAdapter.EmployeeClickListener {

    private lateinit var binding: FragmentEmployeesBinding
    private lateinit var viewModel: EmployeesVM

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EmployeesVM::class.java]
        Utils.getUID(requireActivity())?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeesBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)

        viewModel.getEmployees().invokeOnCompletion {
            val res = viewModel.employeeRes
            binding.employeesEmpty.visibility = View.VISIBLE
            binding.employeesProgress.visibility = View.GONE
            if (res.valid)
                viewModel.employeeRes.employees?.let { employees ->
                    binding.employeesList.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = EmployeesAdapter(requireContext(), employees.toMutableList(), this@Employees)
                    }

                    if (binding.employeesList.adapter!!.itemCount > 0)
                        binding.employeesEmpty.visibility = View.GONE

                    EmployeesVM.employeeList = employees.toMutableList()
                }
            else
                Utils.showSnackbar(coordinatorLayout, res.message)
        }

        binding.employeesAdd.setOnClickListener {
            Utils.showDialog(R.layout.add_employee_dialog, layoutInflater, requireContext()).apply {

                var isListening = false

                val name: TextInputLayout = findViewById(R.id.add_employee_name)
                val phone: TextInputLayout = findViewById(R.id.add_employee_phone)
                val username: TextInputLayout = findViewById(R.id.add_employee_username)
                val password: TextInputLayout = findViewById(R.id.add_employee_password)

                val cancelBtn: Button = findViewById(R.id.add_employee_cancel)
                val progress: ProgressBar = findViewById(R.id.add_employee_progress)
                val saveBtn: Button = findViewById(R.id.add_employee_ok)

                cancelBtn.setOnClickListener { dismiss() }
                saveBtn.setOnClickListener {
                    if (! isListening) {
                        isListening = true
                        name.editText?.addTextChangedListener { Utils.validateField(name, Utils.Companion.VALIDATION_TYPE.TEXT, "Name") }
                        phone.editText?.addTextChangedListener { Utils.validateField(phone, Utils.Companion.VALIDATION_TYPE.PHONE) }
                        username.editText?.addTextChangedListener { Utils.validateField(username, Utils.Companion.VALIDATION_TYPE.TEXT, "Username") }
                        password.editText?.addTextChangedListener { Utils.validateField(password, Utils.Companion.VALIDATION_TYPE.PASSWORD) }
                    }

                    var valid = true
                    if (!Utils.validateField(name, Utils.Companion.VALIDATION_TYPE.TEXT, "Name")) valid = false
                    if (!Utils.validateField(phone, Utils.Companion.VALIDATION_TYPE.PHONE)) valid = false
                    if (!Utils.validateField(username, Utils.Companion.VALIDATION_TYPE.TEXT, "Username")) valid = false
                    if (!Utils.validateField(password, Utils.Companion.VALIDATION_TYPE.PASSWORD)) valid = false

                    if (valid) {
                        cancelBtn.visibility = View.GONE
                        saveBtn.visibility = View.GONE
                        progress.visibility = View.VISIBLE

                        viewModel.addEmployee(
                            username.editText?.text.toString(),
                            password.editText?.text.toString(),
                            name.editText?.text.toString(),
                            phone.editText?.text.toString()
                        ).invokeOnCompletion {
                            val res = viewModel.addRes
                            dismiss()
                            if (res.valid) {
                                val new_emp = Employee(
                                    res.uid.toString(),
                                    name.editText?.text.toString(),
                                    phone.editText?.text.toString()
                                )

                                (binding.employeesList.adapter as EmployeesAdapter).addItem(new_emp)
                                binding.employeesEmpty.visibility = View.GONE
                                EmployeesVM.employeeList.add(new_emp)
                            } else {
                                Utils.showSnackbar(coordinatorLayout, res.message)
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun showInfo(info: Employee, position: Int) {
        Utils.showBottomSheet(R.layout.employees_info, layoutInflater, requireContext()).apply {
            val removeBtn: Button? = findViewById(R.id.employee_info_remove)
            val saveBtn: Button? = findViewById(R.id.employee_info_save)
            val progress: ProgressBar? = findViewById(R.id.employee_info_progress)

            var name: String = info.name
            var phone: String = info.phone
            var isNameValid: Boolean = true
            var isPhoneValid: Boolean = true

            findViewById<TextInputLayout>(R.id.employee_info_name)?.let { layout ->
                layout.editText?.setText(info.name)
                layout.editText?.addTextChangedListener {
                    if (Utils.validateField(layout, Utils.Companion.VALIDATION_TYPE.TEXT, "Name")) {
                        isNameValid = true
                        name = it.toString()
                    }
                    else isNameValid = false

                }
            }

            findViewById<TextInputLayout>(R.id.employee_info_phone)?.let { layout ->
                layout.editText?.setText(info.phone)
                layout.editText?.addTextChangedListener {
                    if (Utils.validateField(layout, Utils.Companion.VALIDATION_TYPE.PHONE)) {
                        isPhoneValid = true
                        phone = it.toString()
                    }
                    else isPhoneValid = false
                }
            }

            saveBtn?.setOnClickListener {
                if (name == info.name && phone == info.phone) this.dismiss()
                else if (isPhoneValid && isNameValid) {
                    saveBtn.visibility = View.GONE
                    removeBtn?.visibility = View.GONE
                    progress?.visibility = View.VISIBLE

                    viewModel.updateEmployee(info.uid, name, phone).invokeOnCompletion {
                        val res = viewModel.res
                        if (res.valid) {
                            (binding.employeesList.adapter as EmployeesAdapter).updateItem(Employee(info.uid, name, phone), position)
                            this.dismiss()
                        } else {
                            this.dismiss()
                            Utils.showSnackbar(coordinatorLayout, res.message)
                        }

                    }
                }
            }

            removeBtn?.setOnClickListener {
                Utils.showDialog(R.layout.remove_employee_dialog, layoutInflater, requireContext()).let { dialog ->
                    val dialogCancel: Button? = dialog.findViewById(R.id.remove_employee_cancel)
                    val dialogProgress: ProgressBar? = dialog.findViewById(R.id.remove_employee_progress)
                    val dialogProceed: Button? = dialog.findViewById(R.id.remove_employee_ok)

                    dialogCancel?.setOnClickListener { this.dismiss() }
                    dialogProceed?.setOnClickListener {
                        dialogCancel?.visibility = View.GONE
                        dialogProceed.visibility = View.GONE
                        dialogProgress?.visibility = View.VISIBLE

                        viewModel.removeEmployee(info.uid).invokeOnCompletion {
                            this.dismiss()
                            dialog.dismiss()
                            val res = viewModel.res

                            if (res.valid) {
                                (binding.employeesList.adapter as EmployeesAdapter).removeItem(position)
                                if (binding.employeesList.adapter!!.itemCount == 0) {
                                    binding.employeesEmpty.visibility = View.VISIBLE
                                }
                            }
                            else
                                Utils.showSnackbar(coordinatorLayout, res.message)
                        }
                    }
                }
            }
        }
    }
}