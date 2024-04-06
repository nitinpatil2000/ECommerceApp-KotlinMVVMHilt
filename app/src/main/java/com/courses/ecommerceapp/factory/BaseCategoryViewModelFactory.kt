package com.courses.ecommerceapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.courses.ecommerceapp.util.Category
import com.courses.ecommerceapp.viewmodel.home.BaseCategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseCategoryViewModel(firestore, category) as T
    }
}