package com.hooni.diettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.hooni.diettracker.di.databaseModule
import com.hooni.diettracker.di.repositoryModule
import com.hooni.diettracker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startKoin {
            androidContext(applicationContext)
            modules(
                viewModelModule,
                databaseModule,
                repositoryModule
            )
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbar).setupWithNavController(navController,appBarConfiguration)
    }
}