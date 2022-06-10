package jp.techacademy.yuuwa.takano.matching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(bottom_navigation, navController)
    }
}

