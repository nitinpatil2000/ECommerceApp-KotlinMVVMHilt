package com.courses.ecommerceapp.fragments.shopping

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
import androidx.navigation.fragment.navArgs
import com.courses.ecommerceapp.data.Address
import com.courses.ecommerceapp.databinding.FragmentAddressBinding
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.billing.AddressViewModel
import com.courses.ecommerceapp.viewmodel.billing.BillingViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {
    private val args by navArgs<AddressFragmentArgs>()
    private lateinit var binding: FragmentAddressBinding
    private val addressViewModel by viewModels<AddressViewModel>()
    private val billingViewModel by viewModels<BillingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.addressList


        if (address == null) {
            onSaveClick()
            binding.buttonDelelte.visibility = View.GONE
            observeAddress()
        } else {
            setInformation(address)
//            updateAddress(address)
//            observeUpdateAddress(address)
            onDeleteClick(address)
            observeDeleteAddress(address)
        }


        //todo close the fragment
        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

    }


    private fun onSaveClick() {
        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()

                val address = Address(addressTitle, fullName, street, phone, city, state)
                addressViewModel.addAddress(address)
            }

        }
    }

    private fun observeAddress() {
        //todo to observe the address state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                addressViewModel.address.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressBarAddress.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressBarAddress.visibility = View.INVISIBLE
                            Snackbar.make(
                                requireView(),
                                "Address SuccessFully Saved",
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            binding.progressBarAddress.visibility = View.INVISIBLE
                        }

                        else -> Unit
                    }
                }
            }
        }


        //todo for error message
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                addressViewModel.error.collectLatest {
                    Snackbar.make(
                        requireView(),
                        it,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setInformation(address: Address) {
        binding.apply {
            edAddressTitle.setText(address.addressTitle)
            edFullName.setText(address.fullName)
            edPhone.setText(address.phone)
            edStreet.setText(address.street)
            edCity.setText(address.city)
            edState.setText(address.state)

            binding.buttonSave.text = "Update"
            binding.buttonDelelte.visibility = View.VISIBLE

        }
    }

    private fun onDeleteClick(address: Address) {
        binding.buttonDelelte.setOnClickListener {
            billingViewModel.deleteAddress(address)
        }
    }

    private fun observeDeleteAddress(address: Address) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                billingViewModel.deleteAddress.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressBarAddress.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressBarAddress.visibility = View.INVISIBLE
                            Snackbar.make(requireView(), "Address Delete Successfully", Snackbar.LENGTH_LONG).show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            binding.progressBarAddress.visibility = View.INVISIBLE
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}