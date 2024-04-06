package com.courses.ecommerceapp.fragments.loginRegister

import android.content.Intent
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
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.activities.ShoppingActivity
import com.courses.ecommerceapp.databinding.FragmentIntroductionBinding
import com.courses.ecommerceapp.viewmodel.launchapp.IntroductionViewModel
import com.courses.ecommerceapp.viewmodel.launchapp.IntroductionViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.courses.ecommerceapp.viewmodel.launchapp.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateState.collect {
                    when (it) {
                        SHOPPING_ACTIVITY -> {
                            Intent(requireContext(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                        ACCOUNT_OPTION_FRAGMENT -> {
                            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }


        binding.startBtn.setOnClickListener {
            viewModel.startButtonClick()
            //first time open the app then go to the account fragment
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
        }
    }
}