package com.example.aadk3_social_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aadk3_social_app.databinding.ActivitySocialBinding
import com.example.aadk3_social_app.fragments.HomeFragment
import com.example.aadk3_social_app.fragments.PostFragment
import com.example.aadk3_social_app.fragments.ProfileFragment

class SocialActivity : AppCompatActivity() {

    private val socialBinding by lazy { ActivitySocialBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(socialBinding.root)

        socialBinding.bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.botNavPost -> setupFragment(PostFragment())
                R.id.botNavProfile -> setupFragment(ProfileFragment())
                else -> setupFragment(HomeFragment())
            }
            true
        }
    }

    private fun setupFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(socialBinding.container.id, fragment)
            .commit()
    }
}
