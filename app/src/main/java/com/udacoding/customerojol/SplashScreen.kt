package com.udacoding.customerojol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.udacoding.customerojol.login.SignInActivity
import org.jetbrains.anko.startActivity

class SplashScreen : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            if(auth?.currentUser?.displayName != null){
                startActivity<MainActivity>()
                finish()
            } else{
                startActivity<SignInActivity>()
                finish()
            }

        },2000)
    }
}
