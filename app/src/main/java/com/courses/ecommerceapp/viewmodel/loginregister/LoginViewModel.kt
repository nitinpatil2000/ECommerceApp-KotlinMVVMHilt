package com.courses.ecommerceapp.viewmodel.loginregister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.util.RegisterValidation
import com.courses.ecommerceapp.util.RegistrationFields
import com.courses.ecommerceapp.util.Resource
import com.courses.ecommerceapp.util.validateEmail
import com.courses.ecommerceapp.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {


    //    we used the shared flow because i send this one time  event to the  ui even if the consumer is present or not
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _loginValidation = Channel<RegistrationFields>()
    val loginValidation = _loginValidation.receiveAsFlow()


    //we send this forgot password event at one time to the ui suppose when i rotate the activity then the mail cannot send second time.
    private val _forgotPassword = MutableSharedFlow<Resource<String>>()
    val forgotPassword = _forgotPassword.asSharedFlow()


    fun loginUser(email: String, password: String) {
//        if (loginValidation(email, password)) {
//
//        } else {
//            viewModelScope.launch {
//                _loginValidation.send(
//                    RegistrationFields(
//                        validateEmail(email),
//                        validatePassword(password)
//                    )
//                )
//
//            }
//        }

        //first complete this code.
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it?.user.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }


    fun resetPassword(email: String) {
        viewModelScope.launch {
            _forgotPassword.emit(Resource.Loading())
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _forgotPassword.emit(Resource.Success(email))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _forgotPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }


    private fun loginValidation(email: String, password: String): Boolean {
        val validateEmail = validateEmail(email)
        val validatePassword = validatePassword(password)
        return validateEmail is RegisterValidation.Success && validatePassword is RegisterValidation.Success
    }

}