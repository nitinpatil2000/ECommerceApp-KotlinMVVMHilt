package com.courses.ecommerceapp.fragments.shopping

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.recyclerview.BestProductAdapter
import com.courses.ecommerceapp.adapters.recyclerview.SearchAdapter
import com.courses.ecommerceapp.databinding.FragmentSearchBinding
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.VerticalItemDecoration
import com.courses.ecommerceapp.util.showBottomNavigationView
import com.courses.ecommerceapp.viewmodel.home.MainCategoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private val bestProductAdapter by lazy {
        BestProductAdapter()
    }
    private val searchAdapter by lazy {
        SearchAdapter()
    }
    private lateinit var inputMethodManger: InputMethodManager
    private val bestProductViewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setUpCategoryRecyclerView()
//        setUpSearchRecyclerView()
//        showKeyboardAutomatically()
//
//        onHomeClick()
//        searchProducts()
//        observeSearch()
//
//        observeCategories()
//        onCancelTvClick()

        //        onSearchTextClick()
//        onCategoryClick()
//
//        binding.frameScan.setOnClickListener {
//            val snackBar =
//                requireActivity().findViewById<CoordinatorLayout>(R.id.snackBar_coordinator)
//            Snackbar.make(
//                snackBar,
//                resources.getText(R.string.g_coming_soon),
//                Snackbar.LENGTH_SHORT
//            ).show()
//        }
//        binding.fragmeMicrohpone.setOnClickListener {
//            val snackBar =
//                requireActivity().findViewById<CoordinatorLayout>(R.id.snackBar_coordinator)
//            Snackbar.make(
//                snackBar,
//                resources.getText(R.string.g_coming_soon),
//                Snackbar.LENGTH_SHORT
//            ).show()
//        }

    }


    private fun setUpCategoryRecyclerView() {
        binding.rvCategories.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
            addItemDecoration(VerticalItemDecoration(40))
        }
    }


    private fun setUpSearchRecyclerView() {
        binding.rvSearch.apply {
            layoutManager =
                LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun showKeyboardAutomatically() {
        inputMethodManger =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )

        binding.edSearch.requestFocus()
    }

    //todo go to the home fragment
    private fun onHomeClick() {
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bnv?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }

    private fun showChancelTv() {
        binding.tvCancel.visibility = View.VISIBLE
        binding.imgMic.visibility = View.GONE
        binding.imgScan.visibility = View.GONE
        binding.fragmeMicrohpone.visibility = View.GONE
        binding.frameScan.visibility = View.GONE
    }

    private fun hideCancelTv() {
        binding.tvCancel.visibility = View.VISIBLE
        binding.imgMic.visibility = View.GONE
        binding.imgScan.visibility = View.GONE
        binding.fragmeMicrohpone.visibility = View.GONE
        binding.frameScan.visibility = View.GONE
    }


    var job: Job? = null
    private fun searchProducts() {
        binding.edSearch.addTextChangedListener { query ->
            val queryTrim = query.toString().trim()
            if (queryTrim.isNotEmpty()) {
                val searchQuery = query.toString().substring(0, 1).toUpperCase()
                    .plus(query.toString().substring(1))
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(500L)
                    bestProductViewModel.searchProducts(searchQuery)
                }
            } else {
                searchAdapter.differ.submitList(emptyList())
                hideCancelTv()
            }
        }
    }


    private fun observeSearch() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bestProductViewModel.search.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            searchAdapter.differ.submitList(it.data)
                            showChancelTv()

                        }

                        is Resource.Error -> {
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

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //collect the latest list
                bestProductViewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarCategories.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            bestProductAdapter.differ.submitList(it.data)
                            binding.progressbarCategories.visibility = View.GONE
                        }

                        is Resource.Error -> {
                            binding.progressbarCategories.visibility = View.GONE
                            Log.d(ContentValues.TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

    }

//    private fun onSearchTextClick() {
//        TODO("Not yet implemented")
//    }

    private fun onCancelTvClick() {
        binding.tvCancel.setOnClickListener {
            searchAdapter.differ.submitList(emptyList())
            binding.edSearch.setText("")
            hideCancelTv()
        }
    }


    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}


