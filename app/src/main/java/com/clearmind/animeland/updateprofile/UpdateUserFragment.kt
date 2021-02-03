package com.clearmind.animeland.updateprofile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseFragment
import com.clearmind.animeland.databinding.FragmentUpdateUserBinding
import com.clearmind.animeland.setting.SettingNavigator
import com.facebook.AccessToken
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpdateUserFragment: BaseFragment<FragmentUpdateUserBinding, UpdateUserViewModel>(), SettingNavigator {

    lateinit var binding: FragmentUpdateUserBinding

    private var accessToken: AccessToken? = null

    override val layoutId: Int
        get() = R.layout.fragment_update_user

    override val bindingVariable: Int
        get() = BR.settingViewModel

    override val viewModel: UpdateUserViewModel
        get() {
            val model : UpdateUserViewModel by viewModel()
            return model
        }

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateUserBindingImpl.inflate(inflater)
        return binding.root
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        accessToken = AccessToken.getCurrentAccessToken()
        return super.onCreateView(inflater,container,savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = mViewDataBinding
    }
    override fun onResume() {
        super.onResume()
        initValues()
        initView()
        initListeners()
    }

    private fun initValues() {
        activity?.applicationContext?.let { initDatabase(it) }
    }

    private fun initListeners() {

        binding.cameraPhotoProfileImageView.setOnClickListener {
            selectImageFromGallery()
        }
    }

    private fun initView() {

    }

    override fun updateUI() {

    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        requireActivity().startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                val bundle: Bundle? = data?.extras
                val bitmap = bundle?.getParcelable<Bitmap>("data")
                binding.profileImageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
