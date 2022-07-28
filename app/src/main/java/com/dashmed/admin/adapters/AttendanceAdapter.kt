package com.dashmed.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.networking.Employee
import com.dashmed.admin.networking.Entry

class AttendanceAdapter (private val context: Context, private var dataset: MutableList<Entry>) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.attendance_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false)
        return AttendanceViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.name.text = dataset[position].name
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun updateDataset(list: MutableList<Entry>) {
        dataset = list
        notifyDataSetChanged()
    }

    fun appendDataset(list: List<Employee>) {
        for (employee: Employee in list) {
            dataset.add(Entry(employee.uid, employee.name))
        }
        notifyItemRangeInserted(dataset.count() - list.count(), list.count())
    }

    fun getDataset(): List<Entry> {
        return dataset
    }

}