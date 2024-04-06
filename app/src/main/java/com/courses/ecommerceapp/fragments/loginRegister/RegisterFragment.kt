package com.courses.ecommerceapp.fragments.loginRegister

import android.os.Bundle
import android.util.Log
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
import com.courses.ecommerceapp.data.User
import com.courses.ecommerceapp.databinding.FragmentRegisterBinding
import com.courses.ecommerceapp.util.RegisterValidation
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.loginregister.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    //initialize the viewModel.
    private val viewModel by viewModels<RegisterViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHaveAccountLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            btnRegisterRegister.setOnClickListener {
                val user = User(
                    //trim function can remove the spaces of the end and start of the string.
                    edtFirstNameRegister.text.toString().trim(),
                    edtLastNameRegister.text.toString().trim(),
                    edtEmailRegister.text.toString().trim()
                )

                //cannot add the trim because password is empty string also.
                val password = edtPasswordRegister.text.toString()

                //call the method in the view model.
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

//        we need to observe this register process using observer lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    //todo you can used the when stmt of all sealed child class to exhaust all scenarious.
                    when (it) {
                        is Resource.Loading -> {
                            binding.btnRegisterRegister.startAnimation()
                        }

                        is Resource.Success -> {
                            //stop the animation
                            binding.btnRegisterRegister.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }

                        is Resource.Error -> {
                            Log.e(TAG, it.message.toString())
                            binding.btnRegisterRegister.revertAnimation()
                        }

                        else -> Unit
                    }
                }
            }
        }

        //lifecycle observer for the validation.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edtEmailRegister.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                        }
                    }

                    if (validation.password is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edtPasswordRegister.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                }
            }
        }
    }
}