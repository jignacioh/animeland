package com.clearmind.animeland.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clearmind.animeland.R
import com.clearmind.animeland.databinding.FragmentUpdateUserBinding
import com.clearmind.animeland.databinding.FragmentUpdateUserBindingImpl

class UpdateUserFragment : Fragment() {

    lateinit var binding: FragmentUpdateUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateUserBindingImpl.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
