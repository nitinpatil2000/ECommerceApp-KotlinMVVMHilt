package com.courses.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.adapters.recyclerview.BestProductAdapter
import com.courses.ecommerceapp.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding

    //we can access this adapter in the sub fragment so we need to initialize this adapter once. using lazy and
    //protected because it is best practice to access this variable in the sub fragments
    protected val offerAdapter: BestProductAdapter by lazy {
        BestProductAdapter()
    }

    protected val bestProductAdapter: BestProductAdapter by lazy {
        BestProductAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up the adapter
        setUpOfferAdapter()
        setUpBestProductsAdapter()

        //this item click is common for all fragments
        offerAdapter.onItemClick = {
            val bundle = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestProductAdapter.onItemClick = {
            val bundle = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        //for horizontal pagination
        binding.rvOffer.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(dx != 0 && !recyclerView.canScrollVertically(1)){
                    offerPagingRequest()
                }
            }
        })

        //for vertical pagination
        binding.nestedScrollBestCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if(v.getChildAt(0).bottom <= v.height * scrollY){
                productPagingRequest()
            }
        })
    }

    //set up adapter
    private fun setUpOfferAdapter() {
        binding.rvOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }

    private fun setUpBestProductsAdapter() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }



    //    for the loading in the sub fragment
    protected fun showOfferLoading(){
        binding.offerProductProgressBar.visibility = View.VISIBLE
    }

    protected fun hideOfferLoading(){
        binding.offerProductProgressBar.visibility = View.GONE
    }

    protected fun showProductLoading(){
        binding.bestProductProgressBar.visibility = View.VISIBLE
    }

    protected fun hideProductLoading(){
        binding.bestProductProgressBar.visibility = View.GONE
    }


    //for the pagination in subFragment
    open fun offerPagingRequest(){

    }

    open fun productPagingRequest(){

    }
}