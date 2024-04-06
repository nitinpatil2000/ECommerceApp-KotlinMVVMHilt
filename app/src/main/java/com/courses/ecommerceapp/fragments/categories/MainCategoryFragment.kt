package com.courses.ecommerceapp.fragments.categories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.recyclerview.BestDealsAdapter
import com.courses.ecommerceapp.adapters.recyclerview.BestProductAdapter
import com.courses.ecommerceapp.adapters.recyclerview.SpecialProductsAdapter
import com.courses.ecommerceapp.databinding.FragmentMainCategoryBinding
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.showBottomNavigationView
import com.courses.ecommerceapp.viewmodel.home.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up the recyclerview
        setUpSpecialProductRecyclerView()
        setUpBestDealsRecyclerView()
        setUpBestProductsRecyclerView()


        //set up onclick to pass the bundle value in the product details fragment.
        specialProductsAdapter.onItemClick = {
            val bundle = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestDealsAdapter.onItemClick = {
            val bundle = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestProductAdapter.onItemClick = {
            val bundle = Bundle()
            bundle.apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }



        //observe the special product data and add in the diff util.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //collect the latest list
                viewModel.specialProduct.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.specialProductProgressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            specialProductsAdapter.differ.submitList(it.data)
                            binding.specialProductProgressBar.visibility = View.GONE
                        }

                        is Resource.Error -> {
                            binding.specialProductProgressBar.visibility = View.GONE
                            Log.d(TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

        //observe the best deal product data and add in the diff util.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //collect the latest list
                viewModel.bestDealProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.bestDealProgressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            bestDealsAdapter.differ.submitList(it.data)
                            binding.bestDealProgressBar.visibility = View.GONE
                        }

                        is Resource.Error -> {
                            binding.bestDealProgressBar.visibility = View.GONE
                            Log.d(TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }

        }

        //observe the best product data and add in the diff util.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //collect the latest list
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.productProgressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            bestProductAdapter.differ.submitList(it.data)
                            binding.productProgressBar.visibility = View.GONE
                        }

                        is Resource.Error -> {
                            binding.productProgressBar.visibility = View.GONE
                            Log.d(TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }


        //for pagination horizontal in special product list
        binding.rvSpecialProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    viewModel.fetchSpecialProducts()
                }
            }
        })


        //for pagination horizontal in best deal product list
        binding.rvBestDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    viewModel.fetchBestDealProducts()
                }
            }
        })

        //for pagination vertical in product list
        binding.pagingScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height * scrollY) {
                viewModel.fetchProductList()
            }
        })


    }


    private fun setUpSpecialProductRecyclerView() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter

        }
    }

    private fun setUpBestDealsRecyclerView() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDeals.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }


    private fun setUpBestProductsRecyclerView() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter

        }
    }

}