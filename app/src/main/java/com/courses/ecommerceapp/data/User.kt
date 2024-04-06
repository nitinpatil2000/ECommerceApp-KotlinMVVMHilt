package com.courses.ecommerceapp.data

data class User(
    val firstName:String,
    val lastName:String,
    val email:String,
    //it is not required also
    val imagePath:String = ""

    //not need password
){
//    create empty constructor for the firebase and pass the empty value
    constructor(): this("","","","")
}
