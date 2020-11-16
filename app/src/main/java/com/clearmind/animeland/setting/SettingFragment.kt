package com.clearmind.animeland.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import coil.load
import coil.transform.CircleCropTransformation
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseFragment
import com.clearmind.animeland.databinding.FragmentSettingBinding
import com.clearmind.animeland.login.LoginActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>(), SettingNavigator {

    lateinit var binding: FragmentSettingBinding

    override val layoutId: Int
        get() = R.layout.fragment_setting

    override val bindingVariable: Int
        get() = BR.settingViewModel

    override val viewModel: SettingViewModel
        get() {
            val model= SettingViewModel()
            return model
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = mViewDataBinding
       // binding = FragmentSettingBindingImpl.inflate(inflater)
        return binding.root
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
            AuthUI.getInstance()
                    .signOut(requireContext().applicationContext)
                    .addOnCompleteListener {
                        val homeIntent = Intent(activity, LoginActivity::class.java)
                        startActivity(homeIntent)
                        activity?.finish()
                    }
        }
    }

    override fun updateUI() {
        TODO("Not yet implemented")
    }

}
