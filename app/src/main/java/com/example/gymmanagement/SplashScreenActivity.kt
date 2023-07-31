@file:Suppress("DEPRECATION")

package com.example.gymmanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.se.omapi.Session
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymmanagement.activity.HomeActivity
import com.example.gymmanagement.activity.LoginActivity
import com.example.gymmanagement.databinding.ActivityMainBinding
import com.example.gymmanagement.ui.theme.GymManagementTheme
import com.techplus.gymmanagement.global.DB
import com.techplus.gymmanagement.manager.SessionManager
import java.lang.Exception

class MainActivity : ComponentActivity() {
    private var mDelayHandler : Handler?=null
    private val splash_delay:Long = 3000
    var db: DB?=null
    var session:SessionManager?=null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DB(this)
        session = SessionManager(this)

        insertAdminData()
        mDelayHandler = Handler()
        mDelayHandler?.postDelayed(mRunnable,splash_delay)
        }

    private val mRunnable:Runnable = Runnable {

        if(session?.isLoggedIn == true){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun insertAdminData(){
        try {
            val sqlCheck = "SELECT * FROM ADMIN"
            db?.fireQuery(sqlCheck)?.use {
                if (it.count>0){
                    Log.d("SplashActivity", "data available")
                }else{
                    val sqlQuery = "INSERT OR REPLACE INTO ADMIN(ID,USER_NAME,PASSWORD,MOBLIE) VALUES('1','Admin','123456','888888888')"
                    db?.executeQuery(sqlQuery)
                }

            }

        }catch (e:Exception){

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelayHandler?.removeCallbacks(mRunnable)
    }
}

