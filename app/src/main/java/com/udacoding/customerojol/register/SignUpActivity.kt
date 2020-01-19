package com.udacoding.customerojol.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.udacoding.customerojol.R
import com.udacoding.customerojol.login.SignInActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class SignUpActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        signUpConfirmPassword.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (signUpConfirmPassword.text.toString() != signUpPassword.text.toString()){
                    signUpConfirmPassword.error = "Password Harus Sama"
                }
            }
        })


        btnRegister.onClick{
            if(signUpName.text.isEmpty() || signUpEmail.text.isEmpty() || signUpHp.text.isEmpty() || signUpPassword.text.isEmpty()){
                alert {
                    title = "Warning !!"
                    message = "Form Tidak Boleh Kosong"
                    noButton {
                        title = "dissmiss"
                    }
                }.show()
            } else if( signUpPassword.text.toString() != signUpConfirmPassword.text.toString() ){
                alert {
                    title = "Warning !!"
                    message = "Password Harus Sama"
                    noButton {
                        title = "dissmiss"
                    }
                }.show()
            }else {
                authUserSignUp(signUpEmail.text.toString(),signUpPassword.text.toString())
            }
        }
    }

    fun authUserSignUp(email: String , passsword : String): Boolean?{
        var status : Boolean? = null
        auth?.createUserWithEmailAndPassword(email, passsword)?.addOnCompleteListener {task ->
            if (task.isSuccessful){
                if(insertDatabase(signUpName.text.toString(),
                        signUpEmail.text.toString(),
                        signUpHp.text.toString(),
                        task.result?.user)
                ){
                    startActivity<SignInActivity>()
                    finish()
                }
            } else {
                println("Failed Add Data To Database")
                status = false
            }
        }
        return status
    }

    fun insertDatabase(name : String , email : String, hp : String, users: FirebaseUser?) : Boolean{

        val user = User()
        user.uid = users?.uid
        user.name = name
        user.email = email
        user.hp = hp

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myRef = database.getReference("User")
        myRef.child(key ?: "").setValue(user)
        return true
    }

}
