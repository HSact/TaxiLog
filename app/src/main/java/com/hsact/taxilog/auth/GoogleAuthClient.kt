@file:Suppress("DEPRECATION")

package com.hsact.taxilog.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hsact.taxilog.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun handleResult(data: Intent?, callback: (GoogleAuthResult) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken!!
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val user = authResult.result?.user
                        callback(GoogleAuthResult.Success(user?.email))
                    } else {
                        callback(GoogleAuthResult.Error(authResult.exception ?: Exception("Unknown error")))
                    }
                }
        } catch (e: Exception) {
            callback(GoogleAuthResult.Error(e))
        }
    }
}