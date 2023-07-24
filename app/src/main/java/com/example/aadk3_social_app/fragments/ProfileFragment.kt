package com.example.aadk3_social_app.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aadk3_social_app.PostAdapter
import com.example.aadk3_social_app.data.User
import com.example.aadk3_social_app.databinding.FragProfileBinding
import com.example.aadk3_social_app.utils.Const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class ProfileFragment: Fragment() {

    private lateinit var profileBinding: FragProfileBinding
    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mFireStore by lazy { FirebaseFirestore.getInstance() }
    private val mFirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private lateinit var userReference: DocumentReference
    private lateinit var imageReference: StorageReference
    private lateinit var currentUser: User
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileBinding = FragProfileBinding.inflate(inflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageReference = mFirebaseStorage.reference.child(Const.STORAGE_PROFILE_IMAGES)
        var posts = mutableListOf<String>()

        // Default first check whether the user has a ProfilePic or not
        userReference = mFireStore.collection(Const.FS_USERS).document(mAuth.currentUser?.uid ?: "")

        userReference.get().addOnSuccessListener { data ->
                data.toObject(User::class.java)?.let { usr ->
                    if (usr.profilePic.isNotBlank()) {
                        // Load this profile pic into the iv
                        Glide.with(this)
                            .load(usr.profilePic)
                            .into(profileBinding.ivProfileImage)
                    }
                    profileBinding.tvFullName.text = usr.fullName
                    profileBinding.tvAbout.text = usr.about
                    profileBinding.tvPosts.text = "${usr.posts.size} Posts"
                    profileBinding.tvFollowers.text = "${usr.followers.size} Followers"
                    profileBinding.tvFollowings.text = "${usr.followings.size} Followings"

                    currentUser = usr
                    posts = usr.posts

                    // Create a recycler View, and load all the images in that RV
                    val adapter = PostAdapter(posts, requireContext())
                    profileBinding.rvPosts.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
                    profileBinding.rvPosts.adapter = adapter
                }
            }

        val profileImagePicker = registerForActivityResult(PickVisualMedia()) { uri ->
            // Lambda gives you a URI of the selected Image
            // Set this to my IV, upload it in my Users Document
            if (uri != null) {
                profileBinding.ivProfileImage.setImageURI(uri)
                val ref = imageReference.child(UUID.randomUUID().toString())
                ref.putFile(uri)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { result ->
                            currentUser.profilePic = result.toString()
                            userReference.set(currentUser)
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }

        profileBinding.ivProfileImage.setOnClickListener {
            // Upload an Image
            profileImagePicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }
}
