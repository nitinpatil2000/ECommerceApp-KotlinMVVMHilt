package com.courses.ecommerceapp.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.courses.ecommerceapp.data.User
import com.courses.ecommerceapp.databinding.FragmentUserAccountBinding
import com.courses.ecommerceapp.dialog.setUpBottomSheetDialog
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.loginregister.LoginViewModel
import com.courses.ecommerceapp.viewmodel.profile.UserAccountViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val userAccountViewModel by viewModels<UserAccountViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultListener: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageActivityResultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo to get the user information
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.userInformation.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showUserLoading()
                        }

                        is Resource.Success -> {
                            hideUserLoading()
                            saveUserInformation(it.data!!)
                        }

                        is Resource.Error -> {
                            hideUserLoading()
                            Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }


        //todo update the user information
        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()

                val user = User(firstName, lastName, email)
                userAccountViewModel.updateUserInformation(user, imageUri)
            }
        }

        //todo open the image on the click listener
        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultListener.launch(intent)
        }

        //todo observe the update states
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.updateUser.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonSave.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.buttonSave.revertAnimation()
                            Snackbar.make(requireView(), "User Profile Update Successfully", Snackbar.LENGTH_LONG).show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            binding.buttonSave.revertAnimation()
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
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

        //todo reset the password.
        binding.tvUpdatePassword.setOnClickListener {
            setUpBottomSheetDialog { email ->
                loginViewModel.resetPassword(email)
            }
        }

        //todo observe the forgot pass state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.forgotPassword.collect {
                    when (it) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Snackbar.make(requireView(), "Password link was sent to your mail", Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(), "Error ${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
        }


        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun saveUserInformation(user: User) {
        binding.apply {
            Glide.with(requireContext()).load(user.imagePath).error(ColorDrawable(Color.BLACK))
                .into(imageUser)
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
            edEmail.setText(user.email)
        }
    }
}