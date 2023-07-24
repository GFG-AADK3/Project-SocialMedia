package com.example.aadk3_social_app.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aadk3_social_app.R
import com.example.aadk3_social_app.data.Post
import com.example.aadk3_social_app.data.User
import com.example.aadk3_social_app.databinding.FragPostBinding
import com.example.aadk3_social_app.utils.Const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostFragment: Fragment() {
    private lateinit var postBinding: FragPostBinding
    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mFireStore by lazy { FirebaseFirestore.getInstance() }
    private val mFirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private lateinit var userReference: DocumentReference
    private lateinit var postReference: StorageReference
    private lateinit var currentUser: User
    private lateinit var selectedUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        postBinding = FragPostBinding.inflate(inflater, container, false)
        return postBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userReference = mFireStore.collection(Const.FS_USERS).document(mAuth.currentUser?.uid ?: "")

        val imagePicker = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedUri = uri
                postBinding.ivPostImage.setImageURI(uri)
            }
        }

        postBinding.ivPostImage.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        postBinding.btnPost.setOnClickListener {
            postBinding.alpha.visibility = View.VISIBLE
            postBinding.pb.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                currentUser = userReference.get().await().toObject(User::class.java) ?: User()

                // Upload the image to Storage
                val postID = UUID.randomUUID().toString()
                postReference = mFirebaseStorage.reference.child("postImages")
                val imgRef = postReference.child(postID)
                imgRef.putFile(selectedUri).await()
                val uri = imgRef.downloadUrl.await()
                val post = Post(
                    imageUrl = uri.toString(),
                    description = postBinding.tietDesc.text.toString(),
                    createdAt = System.currentTimeMillis(),
                )

                // Push the data in the Posts collection
                val postDataRef = mFireStore.collection(Const.FS_POSTS).document(postID)
                postDataRef.set(post).await()

                // Update the users post list
                currentUser.posts.add(uri.toString())
                userReference.set(currentUser).await()

                withContext(Dispatchers.Main) {
                    postBinding.alpha.visibility = View.GONE
                    postBinding.pb.visibility = View.GONE
                    postBinding.tietDesc.text?.clear()
                }
            }
        }
    }
}
