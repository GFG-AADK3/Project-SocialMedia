package com.example.aadk3_social_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aadk3_social_app.databinding.FragHomeBinding

class HomeFragment: Fragment() {
    private lateinit var homeBinding: FragHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeBinding = FragHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }
}
