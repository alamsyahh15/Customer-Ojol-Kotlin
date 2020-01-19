package com.udacoding.customerojol.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udacoding.customerojol.R
import com.udacoding.customerojol.login.SignInActivity
import com.udacoding.customerojol.register.SignUpActivity
import com.udacoding.customerojol.register.User
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity

class ProfileFragment : Fragment() {

    var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User")
        val query = myRef.orderByChild("uid").equalTo(auth?.uid)
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(index in p0.children){
                    val data = index?.getValue(User::class.java)
                    showProfile(data)
                }
            }
        })

        btnLogout.onClick {
            auth?.signOut()
            startActivity<SignInActivity>()
            activity?.finish()
        }
    }

    private fun showProfile(data : User?){
        profileEmail.text = data?.email
        profileName.text = data?.name
        profilePhone.text = data?.hp


    }
}