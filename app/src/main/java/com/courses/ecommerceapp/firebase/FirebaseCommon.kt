package com.courses.ecommerceapp.firebase

import com.courses.ecommerceapp.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    //shortcut
    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("cart")

    fun addToProductInCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }.addOnFailureListener {
                onResult(null, it)
            }
    }


    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        //run transaction can be used read and write the data other hand run batch can be used to only read the data it is a Firebase Transaction.
        firestore.runTransaction { transaction ->

            //get the document ref using the document id present in the cartProduct db
            val documentRef = cartCollection.document(documentId)

            val document = transaction.get(documentRef)

            //get this product then cast this product to the cart product
            val productObject = document.toObject(CartProduct::class.java)

            //if the product object is not null then increase the quantity
            productObject?.let { cartProduct ->

                //when click the add to cart then increase the quantity as 1
                val newQuantity = cartProduct.quantity + 1

                //replace the quantity or increase the quantity and update the existing product using copy extension fun
                //to only change the quantity
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }


    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val cartObject = document.toObject(CartProduct::class.java)
            cartObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newCartObject = cartProduct.copy(quantity = newQuantity)

                transaction.set(documentRef, newCartObject)
            }

        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }


    //todo pass to the when stmt and create the logic for for plus minus button.
    enum class QuantityChanging{
        INCREASE,
        DECREASE
    }
}