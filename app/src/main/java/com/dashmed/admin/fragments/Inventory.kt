package com.dashmed.admin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.admin.InventoryActivity
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.adapters.InventoryAdapter
import com.dashmed.admin.databinding.FragmentInventoryBinding
import com.dashmed.admin.viewmodels.InventoryVM
import com.google.android.material.bottomsheet.BottomSheetDialog


class Inventory : Fragment(), InventoryAdapter.ItemActionListener {

    private lateinit var binding: FragmentInventoryBinding
    private lateinit var viewModel: InventoryVM
    private lateinit var adapter: InventoryAdapter

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[InventoryVM::class.java]
        Utils.getUID(requireActivity())?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)

        adapter = InventoryAdapter(requireContext(), mutableListOf(), inflater, this)
        binding.inventoryList.adapter = adapter
        binding.inventoryList.layoutManager = LinearLayoutManager(requireContext())

        binding.inventoryAdd.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), InventoryActivity::class.java))
        }

        return binding.root
    }

    override fun saveItem(pos: Int, item_id: String, manufactured: String, quantity: Int, dialog: BottomSheetDialog) {
        viewModel.updateItem(item_id, manufactured, quantity).invokeOnCompletion {
            dialog.dismiss()
            val res = viewModel.res
            if (res.valid)
                adapter.updateItem(pos, quantity, manufactured)
            else
                Utils.showSnackbar(coordinatorLayout, res.message)
        }
    }

    override fun deleteItem(pos: Int, item_id: String) {
        Utils.showDialog(R.layout.remove_item_dialog, layoutInflater, requireContext()).let { dialog ->
            val cancelBtn: Button = dialog.findViewById(R.id.remove_item_cancel)
            val removeBtn: Button = dialog.findViewById(R.id.remove_item_ok)
            val progress: ProgressBar = dialog.findViewById(R.id.remove_item_progress)

            cancelBtn.setOnClickListener { dialog.dismiss() }
            removeBtn.setOnClickListener {
                progress.visibility = View.VISIBLE
                cancelBtn.visibility = View.GONE
                removeBtn.visibility = View.GONE

                viewModel.removeItem(item_id).invokeOnCompletion {
                    dialog.dismiss()
                    val res = viewModel.res
                    if (res.valid) {
                        adapter.removeItem(pos)
                        if (adapter.itemCount == 0) {
                            binding.inventoryList.visibility = View.GONE
                            binding.inventoryEmpty.visibility = View.VISIBLE
                        }
                    }
                    else
                        Utils.showSnackbar(coordinatorLayout, res.message)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }

    private fun refreshAdapter() {
        binding.inventoryProgress.visibility = View.VISIBLE
        binding.inventoryList.visibility = View.GONE
        binding.inventoryEmpty.visibility = View.GONE

        viewModel.getInventory().invokeOnCompletion {
            binding.inventoryProgress.visibility = View.GONE

            val res = viewModel.items
            adapter.updateDataset(res.items)
            if (res.items.isNotEmpty())
                binding.inventoryList.visibility = View.VISIBLE
            else
                binding.inventoryEmpty.visibility = View.VISIBLE

            if (! res.valid)
                Utils.showSnackbar(coordinatorLayout, res.message)
        }
    }

}