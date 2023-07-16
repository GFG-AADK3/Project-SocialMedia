package com.example.aadk3_social_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aadk3_social_app.R
import com.example.aadk3_social_app.SocialActivity
import com.example.aadk3_social_app.databinding.FragSignUpBinding
import com.example.aadk3_social_app.utils.isInputValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment: Fragment() {

    private lateinit var signUpBinding: FragSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fs: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        signUpBinding = FragSignUpBinding.inflate(inflater, container, false)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()

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

            if (isValid) {
                signUp(
                    signUpBinding.tietFullName.text.toString(),
                    signUpBinding.tietAbout.text.toString(),
                    signUpBinding.tietUsername.text.toString(),
                    signUpBinding.tietPassword.text.toString()
                )
            }
        }
    }

    private fun signUp(fullName: String, about: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                activity?.startActivity(Intent(activity, SocialActivity::class.java))
                activity?.finish()
            }
            .addOnFailureListener {

            }
    }
}
