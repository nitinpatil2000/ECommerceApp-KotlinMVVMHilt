package com.courses.ecommerceapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.courses.ecommerceapp.R
import com.courses.ecommerceapp.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView() {
    val bottomNavigation =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigation.visibility = View.GONE
}

fun Fragment.showBottomNavigationView() {
    val bottomNavigation =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigation.visibility = View.VISIBLE

}

