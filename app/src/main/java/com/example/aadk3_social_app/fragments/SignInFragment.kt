package com.example.aadk3_social_app.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aadk3_social_app.R
import com.example.aadk3_social_app.SocialActivity
import com.example.aadk3_social_app.databinding.FragSignInBinding
import com.example.aadk3_social_app.utils.isInputValid
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInFragment: Fragment() {

    private lateinit var signInBinding: FragSignInBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        signInBinding = FragSignInBinding.inflate(inflater, container, false)
        return signInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        signInBinding.btnSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SignUpFragment())
                .commit()
        }

        signInBinding.btnLogin.setOnClickListener {

            val isValid = signInBinding.tietUsername.isInputValid(
                parentLayout = signInBinding.tilUsername,
                errorMessage = "Username Invalid"
            ) && signInBinding.tietPassword.isInputValid(
                parentLayout = signInBinding.tilPassword,
                errorMessage = "Password Invalid"
            )

            if (isValid) {
                signIn(
                    signInBinding.tietUsername.text.toString(),
                    signInBinding.tietPassword.text.toString()
                )
            } else {
                return@setOnClickListener
            }
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                /* Goto Dashboard */
                activity?.startActivity(Intent(activity, SocialActivity::class.java))
                activity?.finish()
            }
            .addOnFailureListener { /* Show error */ }
    }
}
