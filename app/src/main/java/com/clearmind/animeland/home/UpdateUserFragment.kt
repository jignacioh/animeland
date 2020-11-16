package com.clearmind.animeland.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clearmind.animeland.databinding.FragmentUpdateUserBinding
import com.clearmind.animeland.databinding.FragmentUpdateUserBindingImpl

class UpdateUserFragment : Fragment() {

    lateinit var binding: FragmentUpdateUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateUserBindingImpl.inflate(inflater)
        return binding.root
    }

}
