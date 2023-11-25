package com.odesa.todo.tasks

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.odesa.todo.R
import androidx.navigation.ui.navigateUp
import com.odesa.todo.databinding.ActivityTasksBinding;

class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        binding = ActivityTasksBinding.inflate( layoutInflater )
        setContentView( binding.root )
        setupNavigationDrawer()
        setSupportActionBar( binding.toolbar )

        val navController: NavController = findNavController( R.id.nav_host_fragment )
        appBarConfiguration = AppBarConfiguration.Builder( R.id.tasks_fragment_dest,
            R.id.tasks_fragment_dest )
            .setOpenableLayout( binding.drawerLayout )
            .build()
        setupActionBarWithNavController( navController, appBarConfiguration )
        binding.navView.setupWithNavController( navController )
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController( R.id.nav_host_fragment ).navigateUp( appBarConfiguration ) ||
                super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        binding.drawerLayout.setStatusBarBackground( R.color.colorPrimaryDark )
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3