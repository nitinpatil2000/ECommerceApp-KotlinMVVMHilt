package com.courses.ecommerceapp.fragments.shopping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.recyclerview.AddressAdapter
import com.courses.ecommerceapp.adapters.recyclerview.BillingAdapter
import com.courses.ecommerceapp.data.Address
import com.courses.ecommerceapp.data.CartProduct
import com.courses.ecommerceapp.data.order.Order
import com.courses.ecommerceapp.data.order.OrderStatus
import com.courses.ecommerceapp.databinding.FragmentBillingBinding
import com.courses.ecommerceapp.util.HorizontalItemDecoration
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.billing.BillingViewModel
import com.courses.ecommerceapp.viewmodel.allorders.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy {
        AddressAdapter()
    }
    private val billingProductAdapter by lazy {
        BillingAdapter()
    }
    private val args by navArgs<BillingFragmentArgs>()
    private var totalPrice = 0f
    private var billingProductList = emptyList<CartProduct>()
    private val billingViewModel by viewModels<BillingViewModel>()

    private var address: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalPrice = args.totalPrice
        billingProductList = args.cartProduct.toList()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.payment) {  //todo true  //true when profile to billing and false when cart fragment to billing.
            binding.rvProducts.visibility = View.INVISIBLE
            binding.bottomLine.visibility = View.INVISIBLE
            binding.totalBoxContainer.visibility = View.INVISIBLE
            binding.buttonPlaceOrder.visibility = View.INVISIBLE
        }

        //todo set up the adapter
        setUpAddressAdapter()
        setUpBillingProductAdapter()

        //todo set the total price
        binding.tvTotalPrice.text = "$ ${String.format("%.2f", totalPrice)}"

        //todo i am pass the list directly because without viewModel because this list comes to in cart fragment.
        billingProductAdapter.differ.submitList(billingProductList)


        //todo observe the address list
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                billingViewModel.addressesList.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.GONE
                            addressAdapter.differ.submitList(it.data)
                        }

                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.GONE
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }

        //todo open the address fragment
        binding.addAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_fragmentAddress)
        }

        //todo close the fragment
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        //todo get the address in this value
        addressAdapter.onItemClick = {
            address = it

            //todo it is true for the profile means you can go to the billing fragment to the profile fragment.
            if (args.payment) {
                val bundle = Bundle().apply {
                    putParcelable("addressList", it)
                }
                findNavController().navigate(R.id.action_billingFragment_to_fragmentAddress, bundle)
            }

        }

        //todo place the order
        binding.buttonPlaceOrder.setOnClickListener {
            if (address == null) {
                Snackbar.make(requireView(), "Please select the address", Snackbar.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

        //todo observe the order status
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.order.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonPlaceOrder.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            Snackbar.make(
                                requireView(),
                                "Order Placed SuccessFully",
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setUpAddressAdapter() {
        binding.rvAddress.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpBillingProductAdapter() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    //todo order confirmation dialog
    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    billingProductList,
                    address!!
                )
                orderViewModel.placeOrder(order)

                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }
}