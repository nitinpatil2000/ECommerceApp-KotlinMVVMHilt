package com.courses.ecommerceapp.data.order

import android.os.Parcelable
import com.courses.ecommerceapp.data.Address
import com.courses.ecommerceapp.data.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val cartProducts: List<CartProduct>,
    val address: Address,

    //for the order screen.
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
) : Parcelable {
    constructor() : this("", 0f, emptyList(), Address())
}
