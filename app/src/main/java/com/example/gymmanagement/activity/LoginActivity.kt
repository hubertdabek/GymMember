package com.example.gymmanagement.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.ActivityLoginBinding
import com.techplus.gymmanagement.global.DB
import com.techplus.gymmanagement.manager.SessionManager
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    var db: DB?=null
    var session:SessionManager?=null

    var edtUserName : EditText?=null
    var edtPassword : EditText?=null


    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DB(this)
        session = SessionManager(this)
        edtUserName = binding.edtUserName
        edtPassword = binding.edtPassword


        binding.btnLogin.setOnClickListener {
            if(validateLogin()){
                getLogin()

            }

        }

        binding.txtForgotPassword.setOnClickListener{

        }
    }
    private fun getLogin(){
        try {
            val sqlQuery="SELECT * FROM ADMIN WHERE USER_NAME='"+edtUserName?.text.toString().trim()+"'AND PASSWORD = '"+edtPassword?.text.toString().trim()+"' AND ID='1'"
            db?.fireQuery(sqlQuery)?.use {
                if(it.count>0){
                    session?.setLogin(true)
                    Toast.makeText(this,"Zalogowano",Toast.LENGTH_LONG).show()
                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    session?.setLogin(false)
                    Toast.makeText(this,"Logowanie nie powiodlo sie", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun  validateLogin():Boolean{
        if(edtUserName?.text.toString().trim().isEmpty()){
            Toast.makeText( this, "Wprowadz Login", Toast.LENGTH_SHORT).show()
            return false
        }else if (edtPassword?.text.toString().trim().isEmpty()){
            Toast.makeText( this, "Wprowadz haslo", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}