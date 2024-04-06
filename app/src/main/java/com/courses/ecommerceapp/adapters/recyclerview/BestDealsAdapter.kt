package com.courses.ecommerceapp.adapters.recyclerview

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.courses.ecommerceapp.data.Product
import com.courses.ecommerceapp.databinding.BestdealRvItemBinding
import com.courses.ecommerceapp.helper.getProductPrice

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(private val binding: BestdealRvItemBinding) :
        ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
//                product.offerPricePercentage?.let {
//                    val remainingPricePercentage = 1f - it
//                    val priceAfterOffer = remainingPricePercentage * product.price
//                    tvPriceAfterPercentage.text= "$ ${String.format("%.1f", priceAfterOffer)}"
//                }

                val priceAfterPercentage =
                    product.offerPricePercentage.getProductPrice(product.price)
                tvPriceAfterPercentage.text = "$ ${String.format("%.1f", priceAfterPercentage)}"

                if (product.offerPricePercentage == null) {
                    tvPriceAfterPercentage.visibility = View.GONE
                } else {
                    tvBestDealPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }

                tvBestDealPrice.text = "$ ${product.price}"
                tvBestDealProductName.text = product.name
            }

        }

    }

    private val diffItemCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffItemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestdealRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    var onItemClick: ((Product) -> Unit)? = null
}