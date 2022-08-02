package com.dashmed.admin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dashmed.admin.MainActivity
import com.dashmed.admin.R
import com.dashmed.admin.Utils
import com.dashmed.admin.databinding.FragmentLoginBinding
import com.dashmed.admin.viewmodels.LoginVM

class Login : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginVM

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.login_coordinator_layout)

        viewModel.username = binding.loginUsername
        viewModel.password = binding.loginPassword

        binding.loginBtn.setOnClickListener {
            if (viewModel.validateFields()) {
                binding.loginBtn.visibility = View.GONE
                binding.loginProgress.visibility = View.VISIBLE

                viewModel.login().invokeOnCompletion {
                    val res = viewModel.res
                    if (! res.valid) {
                        binding.loginProgress.visibility = View.GONE
                        binding.loginBtn.visibility = View.VISIBLE
                        Utils.showSnackbar(coordinatorLayout, res.message)
                    } else {
                        with (requireActivity().getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE).edit()) {
                            putString("UID", res.uid)
                            apply()
                        }
                        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }

        return binding.root
    }
}