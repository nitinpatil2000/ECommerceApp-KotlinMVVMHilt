package com.courses.ecommerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.courses.ecommerceapp.adapters.recyclerview.BillingAdapter
import com.courses.ecommerceapp.data.order.OrderStatus
import com.courses.ecommerceapp.data.order.getOrderStatus
import com.courses.ecommerceapp.databinding.FragmentOrderDetailBinding
import com.courses.ecommerceapp.util.VerticalItemDecoration
import com.courses.ecommerceapp.viewmodel.allorders.OrderViewModel


class OrderDetailsFragment : Fragment() {
    private lateinit var binding:FragmentOrderDetailBinding
    private val viewModel by viewModels<OrderViewModel>()
    private val billingProductAdapter by lazy {
        BillingAdapter()
    }
    private val args by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTheProductListRv()




        val orderList = args.orderList
        binding.apply {
            tvOrderId.text = "Order Id #${orderList.orderId}"
            tvFullName.text = orderList.address.fullName
            tvAddress.text = "${orderList.address.street} ${orderList.address.city}"
            tvPhoneNumber.text = orderList.address.phone

            //todo set up the stepview
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderState =
                when(getOrderStatus(orderList.orderStatus)){
                OrderStatus.Ordered -> 0
                OrderStatus.Confirmed -> 1
                OrderStatus.Shipped -> 2
                OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, true)
            if(currentOrderState == 3){
                stepView.done(true)
            }

            tvTotalPrice.text = "$ ${orderList.totalPrice}"
        }

        billingProductAdapter.differ.submitList(orderList.cartProducts)


        //todo close the fragment
        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setUpTheProductListRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = billingProductAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

}