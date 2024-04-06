package com.courses.ecommerceapp.viewmodel.profile

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.ECommerceApp
import com.courses.ecommerceapp.data.User
import com.courses.ecommerceapp.util.RegisterValidation
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    application: Application
) : AndroidViewModel(application) {


    private val _userInformation = MutableStateFlow<Resource<User>>(Resource.UnexpectedState())
    val userInformation = _userInformation.asStateFlow()

    private val _updateUser = MutableStateFlow<Resource<User>>(Resource.UnexpectedState())
    val updateUser = _updateUser.asStateFlow()

    init {
        getUserInformation()
    }

    fun getUserInformation() {
        viewModelScope.launch {
            _userInformation.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!)

//            .get()
//            .addOnSuccessListener {
//                val userInformation = it.toObject(User::class.java)
//                userInformation?.let {
//                    viewModelScope.launch {
//                        _userInformation.emit(Resource.Success(userInformation))
//                    }
//                }
//            }.addOnFailureListener {
//                viewModelScope.launch {
//                    _userInformation.emit(Resource.Error(it.message.toString()))
//                }
//            }

            //todo update instantly
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _userInformation.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val userInformation = value?.toObject(User::class.java)
                    userInformation?.let {
                        viewModelScope.launch {
                            _userInformation.emit(Resource.Success(userInformation))
                        }
                    }
                }
            }
    }

    fun updateUserInformation(user: User, imageUri: Uri?) {
        val validateInput = validateEmail(user.email) is RegisterValidation.Success &&
                user.firstName.isNotEmpty() &&
                user.lastName.isNotEmpty()

        if (validateInput) {
            viewModelScope.launch {
                _updateUser.emit(Resource.Loading())
            }

            //TODO : if the user not upload the image and have already image in the profile then get this old image and set the user model.
            //TODO : if the user upload the image then upload this image in the user profile.
            if (imageUri == null) {
                saveUserInformation(user, true)
            } else {
                saveUserInformationWithImage(user, imageUri)
            }
        } else {
            viewModelScope.launch {
                _updateUser.emit(Resource.Error("Check Your Inputs"))
                return@launch
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrieveOldImage) {  //TODO :true and note upload the image then set the old image
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateUser.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateUser.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    private fun saveUserInformationWithImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<ECommerceApp>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 97, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val storagePath = storage.child("userImages/${auth.uid}/${UUID.randomUUID()}")
                val result = storagePath.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)

            } catch (e: Exception) {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    //todo logout the application
    fun logOut() {
        auth.signOut()
    }
}
