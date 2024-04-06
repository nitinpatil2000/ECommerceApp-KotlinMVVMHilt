package com.courses.ecommerceapp.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.activities.LoginRegisterActivity
import com.courses.ecommerceapp.databinding.FragmentProfileBinding
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.showBottomNavigationView
import com.courses.ecommerceapp.viewmodel.profile.UserAccountViewModel
import com.google.android.material.snackbar.Snackbar
import com.shuhart.stepview.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<UserAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInformation.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarSettings.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarSettings.visibility = View.GONE
                            Glide.with(requireContext()).load(it.data!!.imagePath)
                                .error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
                            binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                        }

                        is Resource.Error -> {
                            binding.progressbarSettings.visibility = View.GONE
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

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_getAllOrdersFragment)
        }

        binding.linearBilling.setOnClickListener {
            //todo in alternative way to navigate
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray(),
                true
            )
            findNavController().navigate(action)
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logOut()
            startActivity(Intent(requireActivity(), LoginRegisterActivity::class.java))
            requireActivity().finish()
        }

        binding.linearLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_languageFragment)
        }


        //todo set up the version code
        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"
    }


    //becuase hide in the language fragment.
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
    //TODO********************************************************************FINISH() *******************************************************************************

}
