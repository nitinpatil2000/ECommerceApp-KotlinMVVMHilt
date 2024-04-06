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
import com.courses.ecommerceapp.databinding.ProductRvItemBinding
import com.courses.ecommerceapp.helper.getProductPrice

class BestProductAdapter : RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>() {

    inner class BestProductViewHolder(private val binding: ProductRvItemBinding) :
        ViewHolder(binding.root) {
        fun bind(product: Product) {

            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(productImg)
                val priceAfterPercentage =
                    //create a common fun for this
                    product.offerPricePercentage.getProductPrice(product.price)
                //only 2 digits is show after the point
                tvOfferedProductPrice.text =
                    "$ ${String.format("%.1f", priceAfterPercentage)}"


                if (product.offerPricePercentage == null) {
                    tvOfferedProductPrice.visibility = View.GONE
                }else{
                    tvProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                }

                tvProductPrice.text = "$ ${product.price}"
                tvProductName.text = product.name
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
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