package com.dashmed.admin.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.adapters.AddEntryAdapter
import com.dashmed.admin.adapters.AttendanceAdapter
import com.dashmed.admin.databinding.FragmentAttendanceBinding
import com.dashmed.admin.networking.Employee
import com.dashmed.admin.networking.Entry
import com.dashmed.admin.viewmodels.AttendanceVM
import com.dashmed.admin.viewmodels.EmployeesVM
import com.google.android.material.textfield.TextInputEditText
import java.util.*


class Attendance : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding
    private lateinit var viewModel: AttendanceVM

    private lateinit var coordinatorLayout: CoordinatorLayout

    private val calendar = Calendar.getInstance()
    private var date = calendar.get(Calendar.DATE); var month = calendar.get(Calendar.MONTH) + 1; var year = calendar.get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AttendanceVM::class.java]
        Utils.getUID(requireActivity())?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)

        binding.attendanceDate.text = "Date: $date-$month-$year"
        binding.attendanceList.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getAttendance(date, month, year).invokeOnCompletion { updateList() }

        binding.attendanceAddEntry.setOnClickListener {
            Utils.showBottomSheet(R.layout.attendance_entry, layoutInflater, requireContext()).apply {
                val entryList: RecyclerView? = findViewById(R.id.add_entry_list)
                val searchInput: TextInputEditText? = findViewById(R.id.add_entry_search)
                val cancelBtn: Button? = findViewById(R.id.add_entry_cancel)
                val saveBtn: Button? = findViewById(R.id.add_entry_ok)

                val entryEmpList = mutableListOf<Employee>()
                for (employee: Employee in EmployeesVM.employeeList) {
                    (binding.attendanceList.adapter as AttendanceAdapter).getDataset().let {
                        if (! it.contains(Entry(employee.uid, employee.name))) {
                            entryEmpList.add(employee)
                        }
                    }
                }
                entryList?.adapter = AddEntryAdapter(requireContext(), entryEmpList)
                entryList?.layoutManager = LinearLayoutManager(requireContext())

                searchInput?.addTextChangedListener { (entryList?.adapter as AddEntryAdapter).search(searchInput.text.toString()) }
                cancelBtn?.setOnClickListener { dismiss() }
                saveBtn?.setOnClickListener {
                    val selectedEmployees = (entryList?.adapter as AddEntryAdapter).getSelected()
                    val uids = mutableListOf<String>()
                    for (employee: Employee in selectedEmployees) uids.add(employee.uid)
                    if (selectedEmployees.isNotEmpty()) {
                        viewModel.addEntries(uids, year, month, date).invokeOnCompletion {
                            val res = viewModel.res
                            if (res.valid) {
                                (binding.attendanceList.adapter as AttendanceAdapter).appendDataset(selectedEmployees)
                                binding.attendanceEmpty.visibility = View.GONE
                            }
                        }
                    }
                    dismiss()
                }
            }
        }

        binding.attendanceDateContainer.setOnClickListener {
            Utils.showDialog(R.layout.datepicker_dialog, layoutInflater, requireContext()).apply {
                val cancelBtn: Button = findViewById(R.id.datepicker_cancel)
                val proceedBtn: Button = findViewById(R.id.datepicker_ok)
                val datePicker: CalendarView = findViewById(R.id.datepicker_calendar)
                val progress: ProgressBar = findViewById(R.id.datepicker_progress)

                datePicker.maxDate = calendar.timeInMillis
                year = calendar.get(Calendar.YEAR); month = calendar.get(Calendar.MONTH) + 1; date = calendar.get(Calendar.DATE)
                datePicker.setOnDateChangeListener { _, y, m, d -> year = y; month = m + 1; date = d }

                cancelBtn.setOnClickListener { dismiss() }
                proceedBtn.setOnClickListener {
                    cancelBtn.visibility = View.GONE
                    proceedBtn.visibility = View.GONE
                    progress.visibility = View.VISIBLE

                    viewModel.getAttendance(date, month, year).invokeOnCompletion {
                        dismiss()
                        updateList()
                        if (viewModel.attRes.valid)
                            binding.attendanceDate.text = "Date: $date-$month-$year"
                    }
                }
            }
        }
        
        return binding.root
    }

    private fun updateList(): MutableList<Entry>? {
        if (viewModel.attRes.valid)
            viewModel.attRes.employees?.let {
                binding.attendanceList.adapter = AttendanceAdapter(requireContext(), it.toMutableList())
                if (it.isEmpty())
                    binding.attendanceEmpty.visibility = View.VISIBLE
                else
                    binding.attendanceEmpty.visibility = View.GONE
                return it.toMutableList()
            }
        else
            Utils.showSnackbar(coordinatorLayout, viewModel.attRes.message)

        return null
    }
}