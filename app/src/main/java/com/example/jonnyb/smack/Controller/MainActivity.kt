package com.example.jonnyb.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.jonnyb.smack.R
import com.example.jonnyb.smack.Services.AuthService
import com.example.jonnyb.smack.Services.UserDataService
import com.example.jonnyb.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGED))
    }

    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            // This is where we update the nav header ui to reflect the changes
                if(AuthService.isLoggedIn){
                    userNameNavHeader.text = UserDataService.name
                    userEmailNavHeader.text = UserDataService.email
                    val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                    userImageNavHeader.setImageResource(resourceId)
                    userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                    loginBtnNavHeader.text = "Logout"
                }
        }

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View) {

        if(AuthService.isLoggedIn){
            // logout
            UserDataService.logout()
            userNameNavHeader.text = "Login"
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"

        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }


        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    fun addChannelClicked(view: View) {

    }

    fun sendMsgBtnClicked(view: View) {

    }
}
