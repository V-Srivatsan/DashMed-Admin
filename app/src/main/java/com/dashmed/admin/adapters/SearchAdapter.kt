package com.dashmed.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.networking.Medicine

class SearchAdapter(private val context: Context, private var dataset: MutableList<Medicine>, private val listener: ItemClickListener) : RecyclerView.Adapter<SearchAdapter.MedViewHolder>() {

    inner class MedViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.med_item_name)
        val cost: TextView = view.findViewById(R.id.med_item_cost)

        lateinit var med: Medicine

        init {
            view.findViewById<TextView>(R.id.med_item_quantity).visibility = View.GONE
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) { listener.showInfo(med) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.med_item, parent, false)
        return MedViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MedViewHolder, position: Int) {
        val med = dataset[position]
        holder.name.text = med.name
        holder.cost.text = "${med.cost} INR"
        holder.med = med
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun updateDataset(list: List<Medicine>) {
        dataset = list.toMutableList()
        notifyDataSetChanged()
    }

    fun appendDataset(list: List<Medicine>) {
        dataset.addAll(list)
        notifyItemRangeInserted(dataset.count() - list.count(), list.count())
    }

    interface ItemClickListener {
        fun showInfo(med: Medicine)
    }
}