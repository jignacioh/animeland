package com.clearmind.animeland.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.clearmind.animeland.R
import com.clearmind.animeland.databinding.ActivityHomeBinding
import com.clearmind.animeland.extensions.setupWithNavController

class HomeActivity : AppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null

    lateinit var navController : NavController
    lateinit var appBarConfig : AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val bottomNavigationView = binding.bottomNavigationView
        setSupportActionBar(binding.toolbar)

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

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}