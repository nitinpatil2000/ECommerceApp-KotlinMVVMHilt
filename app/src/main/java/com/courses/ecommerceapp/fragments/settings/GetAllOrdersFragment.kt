package com.courses.ecommerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.courses.ecommerceapp.adapters.recyclerview.GetAllOrdersAdapter
import com.courses.ecommerceapp.databinding.FragmentAllOrdersBinding
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.viewmodel.allorders.AllOrdersViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GetAllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentAllOrdersBinding
    private val getAllOrderViewModel by viewModels<AllOrdersViewModel>()
    private val allOrdersAdapter by lazy {
        GetAllOrdersAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo set up the recyclerview
        setUpAllOrdersRecyclerView()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getAllOrderViewModel.allOrders.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarAllOrders.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarAllOrders.visibility = View.GONE
                            allOrdersAdapter.differ.submitList(it.data)
                            if (it.data.isNullOrEmpty()) {
                                binding.tvEmptyOrders.visibility = View.VISIBLE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressbarAllOrders.visibility = View.GONE

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

        //todo go to the order details fragment
        allOrdersAdapter.onItemClick = {
            val action =
                GetAllOrdersFragmentDirections.actionGetAllOrdersFragmentToOrderDetailsFragment(it)
            findNavController().navigate(action)
        }

        //todo close the fragment
        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpAllOrdersRecyclerView() {
        binding.rvAllOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = allOrdersAdapter
        }
    }
}