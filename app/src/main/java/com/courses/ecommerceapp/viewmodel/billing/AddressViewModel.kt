package com.courses.ecommerceapp.viewmodel.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.data.Address
import com.courses.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<Address>>(Resource.UnexpectedState())
    val address = _address.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()


        fun addAddress(address: Address) {
            val validationInput = allInputsAreCorrect(address)

            if (validationInput) {
                viewModelScope.launch { _address.emit(Resource.Loading()) }

                firestore.collection("user").document(auth.uid!!)
                    .collection("address").document()
                    .set(address)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _address.emit(Resource.Success(address))
                        }
                    }.addOnFailureListener {
                        viewModelScope.launch { _address.emit(Resource.Error(it.message.toString())) }
                    }
            } else {
                viewModelScope.launch { _error.emit("All fields are required") }
            }
        }


    //return true
    private fun allInputsAreCorrect(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
    }



}