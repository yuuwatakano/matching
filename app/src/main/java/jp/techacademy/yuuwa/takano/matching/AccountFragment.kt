package jp.techacademy.yuuwa.takano.matching

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account_fragment.login
import kotlinx.android.synthetic.main.activity_account_fragment.logout

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_account_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            login.visibility = View.VISIBLE
            logout.visibility = View.INVISIBLE
        }else{
            login.visibility = View.INVISIBLE
            logout.visibility = View.VISIBLE
            val user = FirebaseAuth.getInstance().currentUser
            Log.v("test_user","ログインした ID:"+ user?.uid!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        Log.v("test_user", user.toString())

        val loginbutton = view.findViewById<Button>(R.id.login)
        loginbutton.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }
        logout.setOnClickListener { _ ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }

}


