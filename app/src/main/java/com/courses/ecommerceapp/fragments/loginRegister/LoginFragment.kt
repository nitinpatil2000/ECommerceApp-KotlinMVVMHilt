package com.courses.ecommerceapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.activities.ShoppingActivity
import com.courses.ecommerceapp.databinding.FragmentLoginBinding
import com.courses.ecommerceapp.dialog.setUpBottomSheetDialog
import com.courses.ecommerceapp.util.RegisterValidation
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.loginregister.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccountRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        //for the forgot password
        binding.tvForgotPasswordLogin.setOnClickListener {
            setUpBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }


//        manage state for the forgot password
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.forgotPassword.collect {
                    when (it) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Snackbar.make(
                                requireView(),
                                "Password link was sent to your mail",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        is Resource.Error -> {
                            Snackbar.make(
                                requireView(),
                                "Error ${it.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

        //get this login value
        binding.apply {
            binding.btnLoginLogin.setOnClickListener {
                val email = binding.edtEmailLogin.text.toString().trim()
                val password = binding.edtPasswordLogin.text.toString()

                viewModel.loginUser(email, password)
            }
        }

        //to collect this value and validate
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.login.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.btnLoginLogin.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.btnLoginLogin.revertAnimation()
                            Intent(requireContext(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                        is Resource.Error -> {
                            binding.btnLoginLogin.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

        //for the validation
        lifecycleScope.launch {
            viewModel.loginValidation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtEmailLogin.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtPasswordLogin.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}
