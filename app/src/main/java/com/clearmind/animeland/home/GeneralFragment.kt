 package com.clearmind.animeland.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clearmind.animeland.databinding.FragmentGeneralBinding
import com.clearmind.animeland.databinding.FragmentGeneralBindingImpl
import com.clearmind.animeland.R


 class GeneralFragment : Fragment() {

     lateinit var binding: FragmentGeneralBinding

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = FragmentGeneralBindingImpl.inflate(inflater)
         return binding.root
     }

     override fun onActivityCreated(savedInstanceState: Bundle?) {
         super.onActivityCreated(savedInstanceState)
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
     }
 }
