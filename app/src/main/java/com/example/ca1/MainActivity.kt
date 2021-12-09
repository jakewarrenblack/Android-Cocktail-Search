package com.example.ca1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // this is the root of our application
        // in here we define our navHostFragment and BottomNavigationView

        // Our navHostFragment in turn contains a reference to the nav_graph, which defines the routes and fragments available to it

        // This works the same way for the bottomNavigationView
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // We've told our nav_graph that the first page it should go to is the SearchFragment, the start destination
        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        nav.setupWithNavController(navController)

    }
}