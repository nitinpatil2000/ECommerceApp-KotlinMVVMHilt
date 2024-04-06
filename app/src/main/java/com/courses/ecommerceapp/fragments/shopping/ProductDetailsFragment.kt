package com.courses.ecommerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.recyclerview.ColorsAdapter
import com.courses.ecommerceapp.adapters.recyclerview.SizesAdapter
import com.courses.ecommerceapp.adapters.viewpager.ViewPager2ImagesAdapter
import com.courses.ecommerceapp.data.CartProduct
import com.courses.ecommerceapp.databinding.FragmentProductDetailsBinding
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.hideBottomNavigationView
import com.courses.ecommerceapp.viewmodel.productdetails.ProductDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.vejei.viewpagerindicator.indicator.CircleIndicator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    //for to get the bundle value you need to set up the args in the navigation xml file firstly.
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter: ViewPager2ImagesAdapter by lazy {
        ViewPager2ImagesAdapter()
    }
    private val colorAdapter: ColorsAdapter by lazy {
        ColorsAdapter()
    }
    private val sizeAdapter: SizesAdapter by lazy {
        SizesAdapter()
    }

    //to invoke the color and size in the adapter
    private var selectedColor: Int? = null
    private var selectedSize: String? = null

    private val viewModel by viewModels<ProductDetailsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo get the bundle value in all others fragment.
        val product = args.product

        setUpViewPagerAdapter()
        setUpColorAdapter()
        setUpSizesAdapter()


        //todo set up the views using bundle
        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            //i am not used the let block bcz it is a viewpager
            viewPagerAdapter.differ.submitList(product.images)

            val colors = product.colors
            colors?.let {
                colorAdapter.differ.submitList(colors)
            }

            val sizes = product.sizes
            sizes?.let {
                sizeAdapter.differ.submitList(sizes)
            }

            if (product.colors.isNullOrEmpty()) {
                binding.tvProductColors.visibility = View.INVISIBLE
            }

            if (product.sizes.isNullOrEmpty()) {
                binding.tvProductsSizes.visibility = View.INVISIBLE
            }
        }


        //todo close the fragment
        binding.closeViewPager.setOnClickListener {
            findNavController().navigateUp()
        }

        //todo invoke the colors and size when the user click
        colorAdapter.onItemClick = {
            selectedColor = it
        }

        sizeAdapter.sizeButtonClick = {
            selectedSize = it
        }


        //todo click the add to cart button
        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        //todo observe the state.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToProduct.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.btnAddToCart.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.btnAddToCart.revertAnimation()
                            binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                            Snackbar.make(
                                requireView(),
                                "Product is added in the cart",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Error -> {
                            binding.btnAddToCart.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

    }

    //todo set up the adapters
    private fun setUpViewPagerAdapter() {
        binding.viewPagerProductDetails.adapter = viewPagerAdapter
        binding.circleIndicator.setWithViewPager2(binding.viewPagerProductDetails)
        binding.circleIndicator.itemCount = args.product.images.size
        binding.circleIndicator.setAnimationMode(CircleIndicator.AnimationMode.SLIDE)

    }

    private fun setUpColorAdapter() {
        binding.rvColors.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            adapter = colorAdapter
        }
    }

    private fun setUpSizesAdapter() {
        binding.rvSize.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sizeAdapter
        }
    }
}