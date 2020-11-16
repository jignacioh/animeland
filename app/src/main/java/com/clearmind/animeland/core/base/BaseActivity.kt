package com.clearmind.animeland.core.base

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.clearmind.animeland.R


abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity(), BaseFragment.Callback {

    val progressDialog by lazy {
        ProgressDialog(this)
    }

    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null

    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int
    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    var builder : AlertDialog.Builder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        lifecycle.addObserver(viewModel)
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        this.mViewModel = if (mViewModel == null) viewModel else mViewModel
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    /**
     * All about permission
     */
    fun checkLocationPermission(): Boolean {
        val result3 = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
        val result4 = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
        return result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED
    }
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(com.clearmind.animeland.R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun showSimpleDialog(title : String, message : String){
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { _, _ ->
                Log.d(
                    "BaseActivity",
                    "Sending atomic bombs to Jupiter"
                )
            }
            .show()
    }

    fun visibleLoaderDialog() {
        var dialog : AlertDialog?=null
        if (builder == null) {
            builder = AlertDialog.Builder(this)
            builder?.setCancelable(false) // if you want user to wait for some process to finish,
            builder?.setView(R.layout.layout_loading_dialog)
            dialog = builder?.create()
            dialog?.show()
        }else{
            dialog?.dismiss()
            builder=null
        }
    }

}



