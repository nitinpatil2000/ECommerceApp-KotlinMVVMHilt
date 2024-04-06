package com.courses.ecommerceapp.viewmodel.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.data.CartProduct
import com.courses.ecommerceapp.firebase.FirebaseCommon
import com.courses.ecommerceapp.helper.getProductPrice
import com.courses.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    //to observe the cart data
    private val _cartList =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.UnexpectedState())
    val cartList = _cartList.asStateFlow()

    //todo get the all products in the cardProductDocuments
    private var cardProductDocuments = emptyList<DocumentSnapshot>()


    //todo calculate the total price of the products using quantity
    val totalProductPrice = cartList.map {
        when (it) {
            is Resource.Success -> {
                calculateTotalPrice(it.data!!)
            }
            //for loading and error state
            else -> null
        }
    }

    //todo for the delete dialog
    private val _dialog = MutableSharedFlow<CartProduct>()
    val dialog = _dialog.asSharedFlow()

    fun deleteProduct(cartProduct: CartProduct) {
        val index = cartList.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cardProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!)
                .collection("cart")
                .document(documentId)
                .delete()
        }

    }

    //in my list have the OfferPercentage or price how can i calculate?
    //used this code to calculate the price of the product when offer percentage is null return the original price or calculate the offer percentage.
    // cartProduct.product.offerPricePercentage.getProductPrice()
    private fun calculateTotalPrice(cartProduct: List<CartProduct>): Float {
        return cartProduct.sumByDouble { cartProduct ->
            (cartProduct.product.offerPricePercentage.getProductPrice(cartProduct.product.price)) * cartProduct.quantity.toDouble()
        }.toFloat()
    }


    init {
        getCartList()
    }

    fun getCartList() {
        firestore.collection("user").document(auth.uid!!)
            .collection("cart")
            //todo it can be run evertime when product is added in the cart icon in the shopping activity i.e, 2, 4 instant update
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _cartList.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cardProductDocuments =
                        value.documents  //to get all the products in the  cardDocuments.

                    val newCartList = value.toObjects(CartProduct::class.java)

                    viewModelScope.launch { //success
                        //get the documents is nothing but the list of records
                        _cartList.emit(Resource.Success(newCartList))
                    }
                }
            }
    }


    //todo very very important function
    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {
        //we will used index to ge the documents.
        //we get the all list from the cart list  and indexOf return the index of the product where you going to change.
        val index = cartList.value.data?.indexOf(cartProduct)
        //get the document id of the documents list
        if (index != null && index != -1) {
            val documentId = cardProductDocuments[index].id

            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartList.emit(Resource.Loading()) }
                    incrementQuantity(documentId)
                }

                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _dialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartList.emit(Resource.Loading()) }
                    decrementQuantity(documentId)

                }

            }
        }
    }

    private fun incrementQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { _, error ->
            if (error != null) {
                viewModelScope.launch { _cartList.emit(Resource.Error(error.message.toString())) }
            }
        }
    }

    private fun decrementQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { _, error ->
            if (error != null) {  //if error is not empty then show this error
                viewModelScope.launch { _cartList.emit(Resource.Error(error.message.toString())) }
            }
        }
    }
}