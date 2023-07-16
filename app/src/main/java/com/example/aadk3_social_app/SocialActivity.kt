package com.example.aadk3_social_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aadk3_social_app.databinding.ActivitySocialBinding

class SocialActivity : AppCompatActivity() {

    private val socialBinding by lazy { ActivitySocialBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(socialBinding.root)
    }
}
