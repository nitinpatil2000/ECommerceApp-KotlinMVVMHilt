package com.courses.ecommerceapp.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.data.Product
import com.courses.ecommerceapp.util.Category
import com.courses.ecommerceapp.util.Constants.Companion.PRODUCTS_COLLECTION
import com.courses.ecommerceapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaseCategoryViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {


    private val _offerProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.UnexpectedState())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.UnexpectedState())
    val bestProducts = _bestProducts.asStateFlow()

    //todo for pagination
    private val offerPagingInfo = PagingInfo()
    private val bestProductPagingInfo = PagingInfo()


    init {
        getOfferProducts()
        getBestProducts()
    }


    fun getOfferProducts() {
        //false
        if (!offerPagingInfo.isProductEnd) {

            viewModelScope.launch {
                _offerProducts.emit(Resource.Loading())
            }
            firestore.collection(PRODUCTS_COLLECTION)
                //when you pass the two queries then you should create a indexing in the firebase.
                .whereEqualTo("category", category.category)
                .whereNotEqualTo("offerPricePercentage", null)
                .limit(offerPagingInfo.pagingCount * 5)
                .get()
                .addOnSuccessListener {
                    val offerList = it.toObjects(Product::class.java)

                    //true and list is same then stored this list
                    offerPagingInfo.isProductEnd =
                        offerPagingInfo.oldProductList == offerList
                    offerPagingInfo.oldProductList = offerList

                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Success(offerList))
                    }

                    offerPagingInfo.pagingCount++


                }.addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }


    fun getBestProducts() {

        //execute only one time bcz by default it is false.
        if (!bestProductPagingInfo.isProductEnd) {

            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("category", category.category)
                .whereEqualTo("offerPricePercentage", null)
                .limit(bestProductPagingInfo.pagingCount * 10)
                .get()
                .addOnSuccessListener {
                    val productsList = it.toObjects(Product::class.java)

                    bestProductPagingInfo.isProductEnd =
                        bestProductPagingInfo.oldProductList == productsList
                    bestProductPagingInfo.oldProductList = productsList

                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(productsList))
                    }

                    bestProductPagingInfo.pagingCount ++;


                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }

    }

}