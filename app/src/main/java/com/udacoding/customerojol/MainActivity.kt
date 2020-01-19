package com.udacoding.customerojol

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.udacoding.customerojol.ui.history.HistoryFragment
import com.udacoding.customerojol.ui.home.HomeFragment
import com.udacoding.customerojol.ui.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val navigationListerner = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId){
            R.id.navigation_home -> {
                setFragment(HomeFragment())
                return@OnNavigationItemSelectedListener  true
            }
            R.id.navigation_history -> {
                setFragment(HistoryFragment())
                return@OnNavigationItemSelectedListener  true
            }
            R.id.navigation_profile -> {
                setFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener  true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragment(HomeFragment())
        nav_view.setOnNavigationItemSelectedListener (navigationListerner)
    }

    fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().
            replace(R.id.container,fragment).commit()
    }
}
