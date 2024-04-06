package com.courses.ecommerceapp.adapters.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.courses.ecommerceapp.databinding.ViewpagerImagesItemsBinding

class ViewPager2ImagesAdapter : RecyclerView.Adapter<ViewPager2ImagesAdapter.ImagesViewHolder>() {

    inner class ImagesViewHolder(private val binding: ViewpagerImagesItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagesList: String?) {
            Glide.with(itemView).load(imagesList).into(binding.imagesList)
        }

    }

    private val differItemCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differItemCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            ViewpagerImagesItemsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val imagesList = differ.currentList[position]
        holder.bind(imagesList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}