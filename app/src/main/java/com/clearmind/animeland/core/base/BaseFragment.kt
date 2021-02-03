package com.clearmind.animeland.core.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.navOptions
import androidx.room.Room
import com.clearmind.animeland.R
import com.clearmind.animeland.core.di.AppDatabase
import com.clearmind.animeland.model.auth.ProfileModel

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<*>> : Fragment() {

    var mActivity: BaseActivity<*, *>? = null

    var mRootView : View? = null

    lateinit var mViewDataBinding : T

    var mViewModel : V? = null

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

    private var db: AppDatabase? =  null

    var profileModel: ProfileModel? = null

    val options = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.mActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //mViewModel=getViewModel()
        //lifecycle.addObserver(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = mViewDataBinding.root
        return mRootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDataBinding.setVariable(bindingVariable, mViewModel)
        mViewDataBinding.lifecycleOwner = this
        mViewDataBinding.executePendingBindings()
    }

    fun initDatabase(context: Context){
        db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database-name"
        ).build()
    }

    fun hideKeyboard() {
        mActivity?.hideKeyboard()
    }

    interface Callback {
        fun onFragmentAttached()
        fun onFragmentDetached()
    }


}