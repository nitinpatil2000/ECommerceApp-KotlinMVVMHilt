package com.courses.ecommerceapp.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.data.Product
import com.courses.ecommerceapp.util.Constants.Companion.PRODUCTS_COLLECTION
import com.courses.ecommerceapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.UnexpectedState())
    val specialProduct: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.UnexpectedState())
    val bestDealProducts: StateFlow<Resource<List<Product>>> = _bestDealProducts

    private val _bestProductList =
        MutableStateFlow<Resource<List<Product>>>(Resource.UnexpectedState())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProductList

    private val specialProductPagingInfo = PagingInfo()
    private val bestDealProductPagingInfo = PagingInfo()
    private val productPagingInfo = PagingInfo()

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.UnexpectedState())
    val search = _search.asStateFlow()

    init {
        fetchSpecialProducts()
        fetchBestDealProducts()
        fetchProductList()
    }

    fun fetchSpecialProducts() {
        if (!specialProductPagingInfo.isProductEnd) {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            firestore
                .collection(PRODUCTS_COLLECTION)
                .whereEqualTo("category", "Special Products")
                .limit(specialProductPagingInfo.pagingCount * 5)
                .get()
                .addOnSuccessListener { result ->
                    //convert this response in my product pojo class.
                    val specialProductList = result.toObjects(Product::class.java)

                    //set to the true and add the new product list in the old product list
                    specialProductPagingInfo.isProductEnd =
                        specialProductPagingInfo.oldProductList == specialProductList
                    specialProductPagingInfo.oldProductList = specialProductList

                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductList))
                    }

                    specialProductPagingInfo.pagingCount++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))

                    }
                }
        }
    }

    fun fetchBestDealProducts() {
        if (!bestDealProductPagingInfo.isProductEnd) {
            viewModelScope.launch {
                _bestDealProducts.emit(Resource.Loading())
            }
            firestore
                .collection(PRODUCTS_COLLECTION)
                .whereEqualTo("category", "Best Deals").limit(productPagingInfo.pagingCount * 5)
                .get()
                .addOnSuccessListener { result ->
                    //convert this response in my product pojo class.
                    val bestDealProductList = result.toObjects(Product::class.java)

                    bestDealProductPagingInfo.isProductEnd =
                        bestDealProductPagingInfo.oldProductList == bestDealProductList
                    bestDealProductPagingInfo.oldProductList = bestDealProductList

                    viewModelScope.launch {
                        _bestDealProducts.emit(Resource.Success(bestDealProductList))
                    }

                    bestDealProductPagingInfo.pagingCount++

                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDealProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchProductList() {
        //if it is false then run this code only one time store this new product list in the old product list.
        //false
        if (!productPagingInfo.isProductEnd) {
            viewModelScope.launch {
                _bestProductList.emit(Resource.Loading())
            }
            firestore
                .collection(PRODUCTS_COLLECTION).limit(productPagingInfo.pagingCount * 10).get()
                .addOnSuccessListener { result ->
                    //convert this response in my product pojo class.
                    val bestProductList = result.toObjects(Product::class.java)

                    //update the boolean value when my old product list and the current list is same then update the value
                    //true
                    productPagingInfo.isProductEnd =
                        productPagingInfo.oldProductList == bestProductList

                    //when it is same then store this all the value in the old product list
                    productPagingInfo.oldProductList = bestProductList


                    viewModelScope.launch {
                        _bestProductList.emit(Resource.Success(bestProductList))
                    }
                    productPagingInfo.pagingCount++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProductList.emit(Resource.Error(it.message.toString()))
                    }

                }
        }
    }


    fun searchProducts(searchQuery: String) {
        viewModelScope.launch {
            _search.emit(Resource.Loading())
        }

        firestore
            .collection(PRODUCTS_COLLECTION).whereEqualTo("name", searchQuery).get()
            .addOnSuccessListener { result ->
                val bestProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _search.emit(Resource.Success(bestProductList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _search.emit(Resource.Error(it.message.toString()))
                }

            }
    }
}

interface data
class PagingInfo(
    var pagingCount: Long = 1,
    var oldProductList: List<Product> = emptyList(),
    var isProductEnd: Boolean = false

)
