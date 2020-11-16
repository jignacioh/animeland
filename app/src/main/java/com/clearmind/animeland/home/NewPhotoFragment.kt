package com.clearmind.animeland.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clearmind.animeland.databinding.FragmentNewPhotoBinding
import com.clearmind.animeland.databinding.FragmentNewPhotoBindingImpl

class NewPhotoFragment : Fragment() {

    lateinit var binding: FragmentNewPhotoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPhotoBindingImpl.inflate(inflater)
        return binding.root
    }

}
