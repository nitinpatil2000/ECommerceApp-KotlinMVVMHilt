package com.courses.ecommerceapp.adapters.recyclerview

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.data.Address
import com.courses.ecommerceapp.databinding.AddressRvItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private var selectedItemPosition = -1

    inner class AddressViewHolder(val binding: AddressRvItemBinding) : ViewHolder(binding.root) {
        fun bind(address: Address, selectedItemPosition: Boolean) {  //it returns true or false
            binding.apply {
                buttonAddress.text = address.addressTitle

                //if it is true then change the background color
                if (selectedItemPosition) {
                    buttonAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                } else {
                    buttonAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }

    }

    private val _differItemCallBack = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, _differItemCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedItemPosition == position)  //it return true

        //todo update the position when i click
        holder.binding.buttonAddress.setOnClickListener {
            if (selectedItemPosition >= 0) {        //greater than 0 or equal to 0 then set the adapter position in the click
                notifyItemChanged(selectedItemPosition)
            }
            selectedItemPosition = holder.adapterPosition
            notifyItemChanged(selectedItemPosition)
            onItemClick?.invoke(address)
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //when we add the new address then cannot click position instantly changed
    var onItemClick: ((Address) -> Unit)? = null

    fun clearSelection(){
        val previousSelectedPosition = selectedItemPosition
        selectedItemPosition = -1
        if(previousSelectedPosition != -1){
            notifyItemChanged(previousSelectedPosition)
        }
    }
}