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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.jonnyb.smack.Model.Channel
import com.example.jonnyb.smack.R
import com.example.jonnyb.smack.Services.AuthService
import com.example.jonnyb.smack.Services.MessageService
import com.example.jonnyb.smack.Services.UserDataService
import com.example.jonnyb.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import com.example.jonnyb.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGED))


    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGED))
        super.onResume()
    }



    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
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
        if (AuthService.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                    .setPositiveButton("Add") { dialogInterface, i ->
                        val addChannelNameEditText = dialogView.findViewById<EditText>(R.id.addChannelNameEditText)
                        val addChannelDescEditText = dialogView.findViewById<EditText>(R.id.addChannelDescEditText)
                        val channelName = addChannelNameEditText.text.toString()
                        val channelDesc = addChannelDescEditText.text.toString()

                        // Create channel with channel name and description
                        socket.emit("newChannel", channelName, channelDesc)



                    }
                    .setNegativeButton("Cancel") { dialogInterface, i ->
                        // cancel and close the dialog

                    }
                    .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->

        runOnUiThread {
            val channelName = args[0] as String
            val channelDesc = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDesc, channelId)
            MessageService.channels.add(newChannel)

            println(newChannel.name)
            println(newChannel.description)
            println(newChannel.id)
        }
    }

    fun sendMsgBtnClicked(view: View) {
        hideKeyboard()
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
