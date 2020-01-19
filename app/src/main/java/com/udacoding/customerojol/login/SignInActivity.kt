package com.udacoding.customerojol.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.udacoding.customerojol.MainActivity
import com.udacoding.customerojol.R
import com.udacoding.customerojol.register.SignUpActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SignInActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        linkSignUp.onClick {
            startActivity<SignUpActivity>()
        }

        btnLogin.onClick {

            if(signInEmail.text.isEmpty() || signInPassword.text.isEmpty())
            {
                alert {
                    title = "Warning !!"
                    message = "Email/Password tidak Boleh Kosong"
                    noButton {
                        title = "dismiss"
                    }


                }.show()
            }else{

                authUserlogin(signInEmail.text.toString(),signInPassword.text.toString())

            }
        }


    }

    private fun authUserlogin(email: String, password: String){
        auth?.signInWithEmailAndPassword(email,password)?.addOnCompleteListener {task ->
            if(task.isSuccessful){
                startActivity<MainActivity>()
                finish()
            }else{
                //finish()
                toast("Login Failed")
            }
        }

    }
}