 package com.clearmind.animeland.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clearmind.animeland.databinding.FragmentGeneralBinding
import com.clearmind.animeland.databinding.FragmentGeneralBindingImpl


 class GeneralFragment : Fragment() {

     lateinit var binding: FragmentGeneralBinding

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = FragmentGeneralBindingImpl.inflate(inflater)
         return binding.root
     }

 }
