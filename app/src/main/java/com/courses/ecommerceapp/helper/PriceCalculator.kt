package com.courses.ecommerceapp.helper

//when my offer percentage is null then also work this function.
fun Float?.getProductPrice(price: Float): Float {  //this refers the offer percentage.
    //when my offer percentage is null then return the original price.
    if (this == null) {
        return price
    }
    val calculatePercentage = 1f - this   // this is just a offer percentage.
    val priceAfterPercentage = calculatePercentage * price
    return priceAfterPercentage
}

