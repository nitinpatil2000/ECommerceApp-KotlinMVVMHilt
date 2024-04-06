package com.courses.ecommerceapp.viewmodel.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.data.Address
import com.courses.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addressesList =
        MutableStateFlow<Resource<List<Address>>>(Resource.UnexpectedState())
    val addressesList = _addressesList.asStateFlow()

    private val _deleteAddress = MutableStateFlow<Resource<Address>>(Resource.UnexpectedState())
    val deleteAddress = _deleteAddress.asStateFlow()

    private var addressItemList = emptyList<DocumentSnapshot>()


    init {
        getAddressList()
    }

    fun getAddressList() {
        viewModelScope.launch { _addressesList.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _addressesList.emit(Resource.Error(error.message.toString())) }
                } else {

                    addressItemList = value?.documents!!

                    val billingProductList = value.toObjects(Address::class.java)
                    viewModelScope.launch {
                        _addressesList.emit(Resource.Success(billingProductList))
                    }
                }
            }
    }

    //todo delete address
    fun deleteAddress(address: Address) {
        viewModelScope.launch { _deleteAddress.emit(Resource.Loading()) }
        val index = addressesList.value.data?.indexOf(address)
        if (index != null && index != -1) {
            val addressId = addressItemList.get(index).id
            firestore.collection("user").document(auth.uid!!)
                .collection("address")
                .document(addressId)
                .delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _deleteAddress.emit(Resource.Success(address))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _deleteAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

//    fun updateAddress(oldAddress: Address, newAddress: Address){
//        viewModelScope.launch {
//            _updateAddress.emit(Resource.Loading())
//        }
//        firestore.findadd
//    }



}