package com.example.homework4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentState = intent.getStringExtra("state")

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_host)

        if(currentState == "home"){
            graph.startDestination = R.id.homeFragment
        }
        else if(currentState == "login"){
            graph.startDestination = R.id.loginFragment
        }

        navHostFragment.navController.graph = graph

    }


}