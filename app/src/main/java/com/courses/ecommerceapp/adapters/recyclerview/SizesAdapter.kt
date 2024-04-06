package com.courses.ecommerceapp.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.courses.ecommerceapp.databinding.SizesRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizeViewHolder>() {

    private var selectedItemPosition = -1

    inner class SizeViewHolder(private val binding: SizesRvItemBinding) : ViewHolder(binding.root) {
        fun bind(sizes: String?, position: Int) {
            binding.tvSize.text = sizes

            if (selectedItemPosition == position) {
                binding.sizeShadow.visibility = View.VISIBLE
            } else {
                binding.sizeShadow.visibility = View.INVISIBLE
            }
        }

    }

    private val diffUtilItemCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtilItemCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(
            SizesRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val sizes = differ.currentList[position]
        holder.bind(sizes, position)

        holder.itemView.setOnClickListener {
            if (selectedItemPosition >= 0) {
                notifyItemChanged(selectedItemPosition)
            }
            selectedItemPosition = holder.adapterPosition
            notifyItemChanged(selectedItemPosition)
            sizeButtonClick?.invoke(sizes)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var sizeButtonClick: ((String) -> Unit)? = null

}