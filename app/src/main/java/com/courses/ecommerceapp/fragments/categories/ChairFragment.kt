package com.courses.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.courses.ecommerceapp.factory.BaseCategoryViewModelFactory
import com.courses.ecommerceapp.util.Category
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.home.BaseCategoryViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<BaseCategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Chair)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //for the offer list
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showOfferLoading()
                        }

                        is Resource.Success -> {
                            hideOfferLoading()
                            offerAdapter.differ.submitList(it.data)
                        }

                        is Resource.Error -> {
                            hideOfferLoading()
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }


        //for the best product list
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showProductLoading()
                        }

                        is Resource.Success -> {
                            hideProductLoading()
                            bestProductAdapter.differ.submitList(it.data)
                        }

                        is Resource.Error -> {
                            hideProductLoading()
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
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

    //for the pagination
    override fun offerPagingRequest() {
        super.offerPagingRequest()
        viewModel.getOfferProducts()
    }

    override fun productPagingRequest() {
        super.productPagingRequest()
        viewModel.getBestProducts()
    }
}