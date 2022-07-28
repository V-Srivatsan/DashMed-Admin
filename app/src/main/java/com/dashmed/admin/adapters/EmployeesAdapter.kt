package com.dashmed.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.networking.Employee
import com.google.android.material.snackbar.Snackbar

class EmployeesAdapter (private val context: Context, private val dataset: MutableList<Employee>, private val listener: EmployeesAdapter.EmployeeClickListener) : RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.employee_name)

        lateinit var info: Employee

        init { view.setOnClickListener(this) }
        override fun onClick(view: View) {
            listener.showInfo(info, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.employees_employee, parent, false)
        return EmployeeViewHolder(layout)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = dataset[position]
        holder.name.text = employee.name
        holder.info = employee
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun addItem(info: Employee) {
        val position = dataset.count()
        dataset.add(info)
        notifyItemInserted(position)
    }

    fun updateItem(info: Employee, position: Int) {
        dataset[position] = info
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        dataset.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getDataset(): List<Employee> {
        return dataset
    }

    interface EmployeeClickListener {
        fun showInfo (info: Employee, position: Int)
    }
}