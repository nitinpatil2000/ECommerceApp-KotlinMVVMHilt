package com.courses.ecommerceapp.data.order

sealed class OrderStatus(val status: String) {
    data object Ordered : OrderStatus("Ordered")
    data object Canceled : OrderStatus("Canceled")
    data object Confirmed : OrderStatus("Confirmed")
    data object Shipped : OrderStatus("Shipped")
    data object Delivered : OrderStatus("Delivered")
    data object Return : OrderStatus("Return")
}


//used this in the AllOrdersAdapter for the change the colors.
fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> {
            OrderStatus.Ordered
        }

        "Canceled" -> {
            OrderStatus.Canceled
        }

        "Confirmed" -> {
            OrderStatus.Confirmed
        }

        "Shipped" -> {
            OrderStatus.Shipped
        }

        "Delivered" -> {
            OrderStatus.Delivered
        }

        else -> OrderStatus.Return

    }
}