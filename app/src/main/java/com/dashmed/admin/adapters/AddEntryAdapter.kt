package com.dashmed.admin.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.networking.Employee

class AddEntryAdapter (private val context: Context, private val employees: List<Employee>) : RecyclerView.Adapter<AddEntryAdapter.EntryHolder>() {

    private val dataset = employees.toMutableList()
    private val selectedEmployees = mutableListOf<Employee>()

    inner class EntryHolder (private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val checkBox = view.findViewById<CheckBox>(R.id.add_entry_checkbox)
        lateinit var info: Employee

        init { view.setOnClickListener(this) }

        override fun onClick(view: View) {
            if (checkBox.isChecked) selectedEmployees.add(info)
            else selectedEmployees.remove(info)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.attendance_entry_item, parent, false)
        return EntryHolder(layout)
    }

    override fun onBindViewHolder(holder: EntryHolder, position: Int) {
        val employee = dataset[position]
        holder.checkBox.text = employee.name
        holder.info = employee
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun search(query: String) {
        for (pos: Int in employees.indices) {
            val data = dataset.getOrNull(pos)
            if (employees[pos].name.contains(query, true)) {
                if (data == null && ! dataset.contains(employees[pos])) {
                    dataset.add(employees[pos])
                    notifyItemInserted(pos)
                } else if (data != null && data != employees[pos]) {
                    dataset[pos] = employees[pos]
                    notifyItemChanged(pos)
                }
            } else if (data != null) {
                dataset.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }
    }

    fun getSelected(): List<Employee> {
        return selectedEmployees
    }

}