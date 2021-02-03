package com.clearmind.animeland.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import coil.Coil
import coil.load
import coil.transform.CircleCropTransformation
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseFragment
import com.clearmind.animeland.databinding.FragmentSettingBinding
import com.clearmind.animeland.login.LoginActivity
import com.facebook.AccessToken
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>(), SettingNavigator {

    private var accessToken: AccessToken? = null
    lateinit var binding: FragmentSettingBinding

    override val layoutId: Int
        get() = R.layout.fragment_setting

    override val bindingVariable: Int
        get() = BR.settingViewModel

    override val viewModel: SettingViewModel
        get() {
            val model : SettingViewModel by viewModel()
            return model
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        accessToken = AccessToken.getCurrentAccessToken()
        //initValues()
        return super.onCreateView(inflater,container,savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = mViewDataBinding
        //lifecycle.addObserver(viewModel)
    }

    private fun initValues(){
        viewModel.loadProfileInformation()
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            profileModel = it
            onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfileInformation()
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            profileModel = it
            initView()
            initListeners()
        }
    }

    private fun initView() {
        profileModel?.let { itProfile ->
            if (itProfile.name != null) {
                binding.userTextView.text = itProfile.name
            }
            binding.mailTextView.text = itProfile.email

            itProfile.photo?.let {itPhoto ->
                if (itPhoto.isNotEmpty()) {
                    binding.profileImageView.load(Uri.parse(itPhoto)) {
                        placeholder(R.drawable.ic_profile_account_128)
                        transformations(CircleCropTransformation())
                    }
                }else {
                    binding.profileImageView.load(R.drawable.ic_profile_account_128, Coil.imageLoader(requireContext()))
                }
            }?: kotlin.run {
                binding.profileImageView.load(R.drawable.ic_profile_account_128, Coil.imageLoader(requireContext()))
            }
        }
    }

    private fun initListeners() {
        profileModel?.let { itProfile ->
            binding.updateTextView.setOnClickListener {
                findNavController().navigate(R.id.userUpdateFragment, null, options)
            }

            binding.logoutTextView.setOnClickListener {
                viewModel.signOutCredentials(itProfile.uid)
                AuthUI.getInstance()
                        .signOut(requireContext().applicationContext)
                        .addOnCompleteListener {
                            val homeIntent = Intent(activity, LoginActivity::class.java)
                            startActivity(homeIntent)
                            activity?.finish()
                        }
            }
        }
    }

    override fun updateUI() {
        TODO("Not yet implemented")
    }
}
