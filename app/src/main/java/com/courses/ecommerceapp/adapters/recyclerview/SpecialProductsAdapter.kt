package com.courses.ecommerceapp.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.courses.ecommerceapp.data.Product
import com.courses.ecommerceapp.databinding.SpecialRvItemBinding

class SpecialProductsAdapter : RecyclerView.Adapter<SpecialProductsAdapter.SpecialViewHolder>() {

    inner class SpecialViewHolder(private val binding: SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(binding.imgSpecialProductItem)
                binding.tvSpecialProductName.text = product.name
                binding.tvSpecialProductPrice.text = product.price.toString()
            }
        }
    }

    //diff util is nothing but the recyclerview make faster. it is not refresh all the item in the recyclerview.
    //it is refresh the item that got the updated.
    private val differItemCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differItemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialViewHolder {
        return SpecialViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SpecialViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    var onItemClick:((Product) ->Unit)?= null


}