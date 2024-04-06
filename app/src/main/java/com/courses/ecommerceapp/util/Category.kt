package com.courses.ecommerceapp.util

sealed class Category(val category:String) {
    data object Chair:Category("Chair")
    data object Table:Category("Tables")
    data object Cupboard:Category("Cupboard")
    data object Accessory:Category("Accessory")
    data object Furniture:Category("Furniture")
}