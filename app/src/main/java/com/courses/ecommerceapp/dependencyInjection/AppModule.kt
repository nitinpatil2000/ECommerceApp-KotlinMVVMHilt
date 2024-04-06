package com.courses.ecommerceapp.dependencyInjection

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.courses.ecommerceapp.firebase.FirebaseCommon
import com.courses.ecommerceapp.util.Constants.Companion.INTRODUCTION_SHARED_PREFERENCE
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
//singleton component is gonna used to stay alive the dependencies as long as the application is alive and activity component is used
// as long as the activity is alive but i used the SingletonComponent.
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides //provide the dependency.
    @Singleton //used the one instance throughout the whole app.
    //this means inside the register view model we need the firebase auth dependency the dagger hilt found this dependency in the appModule.
    //one more thing to create the HiltAndroidApp for the the previlages for the classes models.
    //todo for the authentication.
    fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()

    //for the storage
    @Provides
    @Singleton
    fun provideFirebaseStore() = Firebase.firestore

    //not annotation with single because it is only used in the introduction fragment.
    //for the sharedPreference
    @Provides
    fun provideIntroductionSharedPreference(application: Application) =
        application.getSharedPreferences(INTRODUCTION_SHARED_PREFERENCE, MODE_PRIVATE)!!

    @Provides
    @Singleton
    fun provideFirebaseCommon(firestore: FirebaseFirestore, auth: FirebaseAuth) = FirebaseCommon(firestore, auth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}