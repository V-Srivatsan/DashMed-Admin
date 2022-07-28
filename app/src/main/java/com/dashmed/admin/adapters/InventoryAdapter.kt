package com.dashmed.admin.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.networking.InventoryItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class InventoryAdapter(
    private val context: Context,
    private var dataset: MutableList<InventoryItem>,
    private val inflater: LayoutInflater,
    private val listener: ItemActionListener
) : RecyclerView.Adapter<InventoryAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.med_item_name)
        val quantity: TextView = view.findViewById(R.id.med_item_quantity)
        val cost: TextView = view.findViewById(R.id.med_item_cost)
        val expired: ImageView = view.findViewById(R.id.med_item_expired)

        lateinit var med: InventoryItem
        init { view.setOnClickListener(this) }

        override fun onClick(view: View?) {
            Utils.showBottomSheet(R.layout.med_info, inflater, context).let { dialog ->
                val saveBtn: Button? = dialog.findViewById(R.id.med_info_ok)
                val deleteBtn: Button? = dialog.findViewById(R.id.med_info_remove)
                val manufactured: CalendarView? = dialog.findViewById(R.id.med_info_manufactured)
                val quantity: TextInputLayout? = dialog.findViewById(R.id.med_info_quantity)
                val progress: ProgressBar? = dialog.findViewById(R.id.med_info_progress)
                val expired: ImageView? = dialog.findViewById(R.id.med_item_expired)

                dialog.findViewById<TextView>(R.id.med_info_name)?.text = med.name
                dialog.findViewById<TextView>(R.id.med_info_cost)?.text = "${med.cost} INR"
                dialog.findViewById<TextView>(R.id.med_info_description)?.text = med.description
                dialog.findViewById<ChipGroup>(R.id.med_info_composition)?.let {
                    for (item: String in med.composition) {
                        val chip = Chip(context)
                        chip.text = item
                        it.addView(chip)
                    }
                }

                val calendar = Calendar.getInstance()

                var date = 0; var month = 0; var year = 0
                 with (med.manufactured.split("-")) {
                     year = this[0].toInt()
                     month = this[1].toInt()
                     date = this[2].toInt()
                 }
                val mDate = date; val mMonth = month; val mYear = year

                manufactured?.let {
                    it.maxDate = calendar.timeInMillis
                    it.minDate = Utils.getDateDiff(
                        med.expiration, calendar.get(Calendar.DATE),
                        calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)
                    )
                    it.setOnDateChangeListener { _, y, m, d -> year = y; month = m + 1; date = d }


                    it.date =  SimpleDateFormat("yyyy-MM-dd").parse(med.manufactured).time

                }

                quantity?.editText?.addTextChangedListener {
                    saveBtn?.isEnabled = Utils.validateField(quantity, Utils.Companion.VALIDATION_TYPE.NUMBER, "Quantity")
                }

                quantity?.editText?.setText(med.quantity.toString())

                saveBtn?.setOnClickListener {
                    progress?.visibility = View.VISIBLE
                    saveBtn.visibility = View.GONE
                    deleteBtn?.visibility = View.GONE

                    if (
                        med.quantity != quantity?.editText?.text.toString().toInt() ||
                        (year != mYear || month != mMonth || date != mDate)
                    ) {
                        listener.saveItem(adapterPosition, med.uid, "$year-$month-$date", quantity?.editText?.text.toString().toInt(), dialog)
                    } else
                        dialog.dismiss()
                }

                deleteBtn?.visibility = View.VISIBLE
                deleteBtn?.setOnClickListener {
                    dialog.dismiss()
                    listener.deleteItem(adapterPosition, med.uid)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.med_item, parent, false)
        return ItemViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = item.name
        holder.quantity.text = "Quantity: ${item.quantity}"
        holder.cost.text = "${item.cost} INR"
        holder.med = item

        val calendar = Calendar.getInstance()
        val minDate = Utils.getDateDiff(
            item.expiration, calendar.get(Calendar.DATE),
            calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)
        )
        val manDate = SimpleDateFormat("yyyy-MM-dd").parse(item.manufactured).time

        if (minDate > manDate) {
            holder.expired?.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }


    fun updateDataset(list: List<InventoryItem>) {
        dataset = list.toMutableList()
        notifyDataSetChanged()
    }

    fun appendItems(list: List<InventoryItem>) {
        dataset.addAll(list)
        notifyItemRangeInserted(dataset.count() - list.count(), list.count())
    }

    fun updateItem(position: Int, quantity: Int, manufactured: String) {
        dataset[position].let {
            it.quantity = quantity
            it.manufactured = manufactured
        }
        notifyItemChanged(position)
    }

    fun removeItem(pos: Int) {
        dataset.removeAt(pos)
        notifyItemRemoved(pos)
    }

    interface ItemActionListener {
        fun saveItem(pos: Int, item_id: String, manufactured: String, quantity: Int, dialog: BottomSheetDialog)
        fun deleteItem(pos: Int, item_id: String)
    }

}