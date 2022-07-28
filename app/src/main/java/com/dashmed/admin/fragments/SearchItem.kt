package com.dashmed.admin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.adapters.SearchAdapter
import com.dashmed.admin.databinding.FragmentSearchItemBinding
import com.dashmed.admin.networking.Medicine
import com.dashmed.admin.viewmodels.SearchItemVM
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class SearchItem : Fragment(), SearchAdapter.ItemClickListener {

    private lateinit var binding: FragmentSearchItemBinding
    private lateinit var viewModel: SearchItemVM

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SearchItemVM::class.java]
        Utils.getUID(requireActivity())?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchItemBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.inventory_coordinator_layout)

        val adapter = SearchAdapter(requireContext(), listOf<Medicine>().toMutableList(), this)
        binding.inventorySearchList.adapter = adapter
        binding.inventorySearchList.layoutManager = LinearLayoutManager(requireContext())

        binding.inventorySearchInput.editText?.addTextChangedListener {
            binding.inventorySearchList.visibility = View.GONE
            binding.inventorySearchNone.visibility = View.GONE
            if (it.toString().isNotEmpty()) {
                binding.inventorySearchProgress.visibility = View.VISIBLE
                binding.inventorySearchEmpty.visibility = View.GONE

                viewModel.search(it.toString())?.invokeOnCompletion {
                    viewModel.searchJob?.let { job ->
                        if (! job.isCancelled) {
                            adapter.updateDataset(viewModel.medRes.medicines)
                            binding.inventorySearchProgress.visibility = View.GONE
                            if (adapter.itemCount == 0)
                                binding.inventorySearchNone.visibility = View.VISIBLE
                            else
                                binding.inventorySearchList.visibility = View.VISIBLE
                        }
                    }
                }
            } else
                binding.inventorySearchEmpty.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun showInfo(med: Medicine) {
        val calendar = Calendar.getInstance()
        Utils.showBottomSheet(R.layout.med_info, layoutInflater, requireContext()).let { dialog ->
            val saveBtn: Button? = dialog.findViewById(R.id.med_info_ok)
            val manufactured: CalendarView? = dialog.findViewById(R.id.med_info_manufactured)
            val quantity: TextInputLayout? = dialog.findViewById(R.id.med_info_quantity)
            val progress: ProgressBar? = dialog.findViewById(R.id.med_info_progress)

            dialog.findViewById<TextView>(R.id.med_info_name)?.text = med.name
            dialog.findViewById<TextView>(R.id.med_info_cost)?.text = "${med.cost} INR"
            dialog.findViewById<TextView>(R.id.med_info_description)?.text = med.description
            dialog.findViewById<ChipGroup>(R.id.med_info_composition)?.let {
                for (item: String in med.composition) {
                    val chip = Chip(requireContext())
                    chip.text = item
                    it.addView(chip)
                }
            }

            var date = calendar.get(Calendar.DATE); var month = calendar.get(Calendar.MONTH) + 1; var year = calendar.get(Calendar.YEAR)
            manufactured?.maxDate = calendar.timeInMillis
            manufactured?.minDate = Utils.getDateDiff(med.expiration, date, month, year)
            manufactured?.setOnDateChangeListener { _, y, m, d -> year = y; month = m + 1; date = d }

            quantity?.editText?.addTextChangedListener {
                saveBtn?.isEnabled = Utils.validateField(quantity, Utils.Companion.VALIDATION_TYPE.NUMBER, "Quantity")
            }

            saveBtn?.setOnClickListener {
                saveBtn.visibility = View.GONE
                progress?.visibility = View.VISIBLE

                viewModel.addItem(med.uid, year, month, date, quantity?.editText?.text.toString().toInt()).invokeOnCompletion {
                    dialog.dismiss()
                    val res = viewModel.res
                    if (res.valid)
                        Utils.showSnackbar(coordinatorLayout, "Item successfully added to inventory!")
                    else
                        Utils.showSnackbar(coordinatorLayout, res.message)
                }
            }
        }
    }


}