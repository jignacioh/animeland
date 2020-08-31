package com.clearmind.animeland.setting

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import coil.load
import coil.transform.CircleCropTransformation
import com.clearmind.animeland.R
import com.clearmind.animeland.databinding.FragmentSettingBinding
import com.clearmind.animeland.databinding.FragmentSettingBindingImpl
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBindingImpl.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        initView()
        initListeners()
    }

    private fun initView() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            binding.userTextView.text = user.displayName

            binding.mailTextView.text = user.email

            val photoUrl: Uri? = user.photoUrl
            photoUrl.let {
                binding.profileImageView.load(it) {
                    crossfade(true)
                    placeholder(R.drawable.ic_profile_account_128)
                    transformations(CircleCropTransformation())
                }
            }
            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = user.uid
        }
    }

    private fun initListeners() {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
        binding.updateTextView.setOnClickListener {
            findNavController().navigate(R.id.userUpdateFragment, null, options)
        }

        binding.logoutTextView.setOnClickListener {

        }
    }

}
