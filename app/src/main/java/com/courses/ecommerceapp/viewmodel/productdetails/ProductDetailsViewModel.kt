package com.courses.ecommerceapp.viewmodel.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.data.CartProduct
import com.courses.ecommerceapp.firebase.FirebaseCommon
import com.courses.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(    //todo FireStore is used to store the data and auth can be used to stored the cart data for particular person.
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {


    private val _addToProduct = MutableStateFlow<Resource<CartProduct>>(Resource.UnexpectedState())
    val addToProduct = _addToProduct.asStateFlow()

    //todo add the cart product and update the quantity of existing product
    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch {
            _addToProduct.emit(Resource.Loading())
        }

        //todo get the particular user user_id and check the product id is already given to the cart then increase the quantity otherwise
        // add the new product
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get().
            addOnSuccessListener {
                //todo it return the documents of the user and the product when it is present in the cart.
                it.documents?.let {
                    if (it.isEmpty()) { //add the new product
                        addNewProduct(cartProduct)
                    } else {
                        val productIsPresent = it.first().toObject(CartProduct::class.java)
                        if (productIsPresent == cartProduct) {   //increase the quantity
                            //to get the id
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else { //add the new product.
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _addToProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addToProductInCart(cartProduct) { addedProduct, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToProduct.emit(Resource.Success(addedProduct))
                } else { //when exception is not null then show this exception in the emit block
                    _addToProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }


    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToProduct.emit(Resource.Success(cartProduct))
                } else {
                    _addToProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }
}


//TODO NOTE: we gonna create a add new product and increase quantity logic in the firebase package bcz other classes and fragments are
// used this functions.