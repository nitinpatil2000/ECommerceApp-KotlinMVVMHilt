package com.courses.ecommerceapp.adapters.recyclerview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.courses.ecommerceapp.data.CartProduct
import com.courses.ecommerceapp.databinding.CartProductItemBinding
import com.courses.ecommerceapp.helper.getProductPrice

class CartProductAdapter:RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {

    inner class CartProductViewHolder(val binding:CartProductItemBinding):ViewHolder(binding.root){
        fun bind(cartProduct: CartProduct, position: Int) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imgCardProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

//                cartProduct.product.offerPricePercentage is a  todo this
                val priceAfterPercentage = cartProduct.product.offerPricePercentage.getProductPrice(cartProduct.product.price)

                tvProductCartPrice.text =
                    "$ ${String.format("%.2f", priceAfterPercentage)}"

                imgCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?:Color.TRANSPARENT))
                tvCartProductSize.text = cartProduct.selectedSize?:"".apply {
                    imgCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))

                }

            }
        }

    }

    private val differItemCallback = object :DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }


        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differItemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
       return CartProductViewHolder(
           CartProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
       )
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct, position)

        //todo when i click to the product then go to the product details fragment show the carProduct details in the productDetailsFrag.
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(cartProduct)
        }

        holder.binding.minusIcon.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.plusIcon.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    var onItemClick :((CartProduct)-> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick:((CartProduct) -> Unit)? = null

}
