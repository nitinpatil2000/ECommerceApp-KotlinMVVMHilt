package com.courses.ecommerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.viewpager.HomeViewpagerAdapter
import com.courses.ecommerceapp.databinding.FragmentHomeBinding
import com.courses.ecommerceapp.fragments.categories.AccessoryFragment
import com.courses.ecommerceapp.fragments.categories.ChairFragment
import com.courses.ecommerceapp.fragments.categories.CupboardFragment
import com.courses.ecommerceapp.fragments.categories.FurnitureFragment
import com.courses.ecommerceapp.fragments.categories.MainCategoryFragment
import com.courses.ecommerceapp.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        //disabled the swapping of viewpager
        binding.viewPager.isUserInputEnabled = false

        val viewPagerAdapter =
            HomeViewpagerAdapter(categoriesFragment, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }

}