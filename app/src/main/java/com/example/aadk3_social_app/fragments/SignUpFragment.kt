package com.example.aadk3_social_app.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aadk3_social_app.R
import com.example.aadk3_social_app.SocialActivity
import com.example.aadk3_social_app.data.User
import com.example.aadk3_social_app.databinding.FragSignUpBinding
import com.example.aadk3_social_app.utils.Const
import com.example.aadk3_social_app.utils.isInputValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpFragment: Fragment() {

    private lateinit var signUpBinding: FragSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        signUpBinding = FragSignUpBinding.inflate(inflater, container, false)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        signUpBinding.btnLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SignInFragment())
                .commit()
        }

        signUpBinding.btnSignUp.setOnClickListener {

            val isValid = signUpBinding.tietFullName.isInputValid(
                parentLayout = signUpBinding.tilFullName,
                errorMessage = "Please enter your Full Name"
            ) && signUpBinding.tietAbout.isInputValid(
                parentLayout = signUpBinding.tilAbout,
                errorMessage = "Please enter something about yourself"
            ) && signUpBinding.tietUsername.isInputValid(
                parentLayout = signUpBinding.tilUsername,
                errorMessage = "Email address invalid"
            ) && signUpBinding.tietPassword.isInputValid(
                parentLayout = signUpBinding.tilPassword,
                errorMessage = "Enter a valid Password"
            )

            lifecycleScope.launch(Dispatchers.IO) {
//                val x = async { Log.d("","") }
//                x.await()
                // All the Room, Retrofit, and Firebase KTX, are basically async functions

                // Loader Icon
                signUpUsingCoroutines(
                    signUpBinding.tietFullName.text.toString(),
                    signUpBinding.tietAbout.text.toString(),
                    signUpBinding.tietUsername.text.toString(),
                    signUpBinding.tietPassword.text.toString()
                )
                withContext(Dispatchers.Main) {
                    // TODO: Check whether the code comes into this block or not ()
                    // Remove the loader
                }
            }

//            if (isValid) {
//                signUp(
//                    signUpBinding.tietFullName.text.toString(),
//                    signUpBinding.tietAbout.text.toString(),
//                    signUpBinding.tietUsername.text.toString(),
//                    signUpBinding.tietPassword.text.toString()
//                )
//            }
        }
    }

    private fun signUp(fullName: String, about: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                // Add this user data in my Firestore
                mFirestore
                    .collection("Users")
                    .document(result.user?.uid ?: "EmptyDocID")
                    .set(User(fullName = fullName, email = email, about = about))
                    .addOnSuccessListener {
                        // Navigate to Feeds Activity
                        activity?.startActivity(Intent(activity, SocialActivity::class.java))
                        activity?.finish()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { err ->
                Toast.makeText(activity, err.message, Toast.LENGTH_SHORT).show()
            }
    }

    private suspend fun signUpUsingCoroutines(fullName: String, about: String, email: String, password: String) {
        try {
            val authResult = mAuth.createUserWithEmailAndPassword(email, password).await()

            mFirestore.collection(Const.FS_USERS)
                .document(authResult.user?.uid ?: "EmptyDocID")
                .set(User(fullName = fullName, email = email, about = about)).await()

            switchUiScreens()
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchUiScreens() {
        activity?.startActivity(Intent(activity, SocialActivity::class.java))
        activity?.finish()
    }
}
