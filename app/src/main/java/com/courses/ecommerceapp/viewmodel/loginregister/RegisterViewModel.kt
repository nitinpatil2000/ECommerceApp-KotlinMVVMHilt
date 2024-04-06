package com.courses.ecommerceapp.viewmodel.loginregister

import androidx.lifecycle.ViewModel
import com.courses.ecommerceapp.data.User
import com.courses.ecommerceapp.util.Constants.Companion.USER_COLLECTION
import com.courses.ecommerceapp.util.RegisterValidation
import com.courses.ecommerceapp.util.RegistrationFields
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.validateEmail
import com.courses.ecommerceapp.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseFirestore
) : ViewModel() {

    //create a generic sealed class for the error response T means it accepts any data types.
    //todo mutable state flow hold the current state of the registration i.e., unspecified. change the state immediately i.e, success, error, loading.
    private val _register = MutableStateFlow<Resource<User>>(Resource.UnexpectedState())
    //flow is readable only for the security and best practice.
    val register: Flow<Resource<User>> = _register


    //channel continues send the value even the consumer is consumes or not.
    private val _validation = Channel<RegistrationFields>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            //run blocking is executed until the loading is not complete. this is a coroutine scope
            // because you emit is a suspend function.
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                //when the registration was successful
                .addOnSuccessListener {
                    //let keyword is used to elliminate the null related issues and create cleaner and maintainable the code.
                    it.user?.let {
                        saveDataInDb(it.uid, user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registrationFields = RegistrationFields(validateEmail(user.email), validatePassword(password))
            runBlocking {
                _validation.send(registrationFields)
            }
        }
    }

    private fun saveDataInDb(userId: String, user: User) {
        firebaseDb.collection(USER_COLLECTION)
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }


    //fun for the validation and return the true false value.
    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }

}