package com.courses.ecommerceapp.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.recyclerview.CartProductAdapter
import com.courses.ecommerceapp.databinding.FragmentCartBinding
import com.courses.ecommerceapp.firebase.FirebaseCommon
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.VerticalItemDecoration
import com.courses.ecommerceapp.viewmodel.cart.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter: CartProductAdapter by lazy {
        CartProductAdapter()
    }

    //i call the CartViewModel getCartProduct fun in the shopping activity so i did not call this fun again and again in the frag.
    //so we need to pass the Activity viewModel to create the instance of the object and call the api once.

    private val viewModel by activityViewModels<CartViewModel>()
    var totalPrice = 0f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up the recyclerview
        setUpCartRecyclerView()

        //todo close the fragment
        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        //todo observe the cart list
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartList.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarCart.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            if (it.data!!.isEmpty()) {
                                showEmptyCart()
                                hideOtherViews()
                            } else {
                                hideEmptyCart()
                                showOtherViews()
                                cartAdapter.differ.submitList(it.data)
                            }

                        }

                        is Resource.Error -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }

        }

        //todo calculate the price and observe this
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalProductPrice.collectLatest { totalProductPrice ->
                    totalProductPrice?.let {
                        //todo i want to pass this value in the billing fragment.
                        totalPrice = it
                        binding.tvTotalPrice.text = "$ ${String.format("%.2f", totalPrice)}"
                    }
                }
            }
        }

        //todo for plus and minus button click
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        //todo quantity is 1 then delete the dialog
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dialog.collectLatest {
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete Item From Cart")
                        setMessage("Do you want to delete this item from cart?")
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteProduct(it)
                            dialog.dismiss()
                        }
                    }

                    alertDialog.create()
                    alertDialog.show()
                }
            }
        }

        //todo open the productDetailsFragment and show the cartProductInformation
        cartAdapter.onItemClick = { cartProduct ->
            val bundle = Bundle().apply {
                putParcelable("product", cartProduct.product)
            }

            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        //todo click this button to open the billing fragment
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,
                cartAdapter.differ.currentList.toTypedArray(), false)
            findNavController().navigate(action)
        }

    }

    private fun showEmptyCart() {
        binding.layoutCarEmpty.visibility = View.VISIBLE
    }

    private fun hideEmptyCart() {
        binding.layoutCarEmpty.visibility = View.GONE
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }


    private fun setUpCartRecyclerView() {
        binding.rvCart.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}