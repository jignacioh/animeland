package com.clearmind.animeland.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clearmind.animeland.databinding.FragmentHomeBinding
import com.clearmind.animeland.databinding.FragmentHomeBindingImpl

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBindingImpl.inflate(inflater)
        return binding.root
    }

}
