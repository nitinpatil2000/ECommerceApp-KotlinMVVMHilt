package com.courses.ecommerceapp.adapters.recyclerview

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.courses.ecommerceapp.databinding.ColorsRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {
    private var selectedItemPosition = -1

    inner class ColorsViewHolder(private val binding: ColorsRvItemBinding) :
        ViewHolder(binding.root) {
        fun bind(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)


            if (position == selectedItemPosition) { //color is selected
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            } else {  //colors is not selected
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }
            }
        }

    }

    private val differItemCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differItemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorsRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val colors = differ.currentList[position]
        holder.bind(colors, position)

        holder.itemView.setOnClickListener {
            if (selectedItemPosition >= 0) {
                notifyItemChanged(selectedItemPosition)
            }
            selectedItemPosition = holder.adapterPosition
            notifyItemChanged(selectedItemPosition)
            onItemClick?.invoke(colors)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    //assign the fun in the variable which get the all colors.
    var onItemClick: ((Int) -> Unit)? = null


}