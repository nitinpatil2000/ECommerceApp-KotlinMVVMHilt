package com.courses.ecommerceapp.adapters.recyclerview

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.data.order.Order
import com.courses.ecommerceapp.data.order.OrderStatus
import com.courses.ecommerceapp.data.order.getOrderStatus
import com.courses.ecommerceapp.databinding.OrderItemBinding

class GetAllOrdersAdapter : RecyclerView.Adapter<GetAllOrdersAdapter.GetAllOrdersViewHolder>() {

    inner class GetAllOrdersViewHolder(val binding: OrderItemBinding) : ViewHolder(binding.root) {
        fun bind(ordersList: Order) {
            binding.apply {
                tvOrderId.text = ordersList.orderId.toString()
                tvOrderDate.text = ordersList.date

                val resources = itemView.resources

                val colorDrawable = when (getOrderStatus(ordersList.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }

                    is OrderStatus.Confirmed,
                    is OrderStatus.Delivered,
                    is OrderStatus.Shipped -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }

                    is OrderStatus.Canceled,
                    is OrderStatus.Return -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                }
                imageOrderState.setImageDrawable(colorDrawable)
                tvTotalProductCount.text = "Total Product ->  ${ordersList.cartProducts.size}"

                val totalPrice = ordersList.totalPrice
                tvToalProductPrice.text = "Total Price -> $ ${String.format("%.2f", totalPrice)}"

            }

        }

    }

    private val _differItemCallBack = object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.cartProducts == newItem.cartProducts
        }


        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, _differItemCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetAllOrdersViewHolder {
        return GetAllOrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GetAllOrdersViewHolder, position: Int) {
        val ordersList = differ.currentList[position]
        holder.bind(ordersList)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(ordersList)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Order) -> Unit)? = null


}