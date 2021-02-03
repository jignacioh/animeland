package com.clearmind.animeland.home

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseActivity
import com.clearmind.animeland.databinding.ActivityHomeBinding
import com.clearmind.animeland.extensions.setupWithNavController
import com.clearmind.animeland.model.auth.ProfileModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity: BaseActivity<ActivityHomeBinding, HomeViewModel>(), HomeNavigator {

    private var currentNavController: LiveData<NavController>? = null
    lateinit var navController : NavController
    lateinit var appBarConfig : AppBarConfiguration
    //private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var profileModel: ProfileModel

    override val viewModel: HomeViewModel
        get() {
            val model: HomeViewModel by viewModel()
            return model
        }

    override val layoutId: Int
        get() = R.layout.activity_home
    override val bindingVariable: Int
        get() = BR.homeViewModel

    lateinit var mActivityTasksBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileModel = intent.getSerializableExtra("USER") as ProfileModel
        mActivityTasksBinding = viewDataBinding!!
        viewModel.setNavigator(this)
        lifecycle.addObserver(viewModel)

        val bottomNavigationView = mActivityTasksBinding.bottomNavigationView
        setSupportActionBar(mActivityTasksBinding.toolbar)

        //navController = findNavController(R.id.nav_home_fragment)
        val navGraphIds = listOf(R.navigation.home_navigation, R.navigation.general_navigation, R.navigation.upload_navigation, R.navigation.setting_navigation)
        //Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.nav_home_fragment,
                intent = intent
        )

        /*
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            // compare destination id
            title = when (destination.id) {
                R.id.homeFragment -> getString(R.string.label_home_fragment)
                R.id.generalFragment -> getString(R.string.label_general_fragment)
                R.id.uploadFragment -> getString(R.string.label_upload_fragment)
                else -> getString(R.string.label_upload_fragment)
            }
        }

        appBarConfig = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfig)

        bottomNavigationView.setupWithNavController(navController)*/

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onStart() {
        super.onStart()
        initValues(profileModel)
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached() {

    }

    private fun initValues(profileModel: ProfileModel) {
        viewModel.persistUserAuthenticated(profileModel)
    }
    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun doSomething() {

    }
}