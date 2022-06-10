package jp.techacademy.yuuwa.takano.matching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account_fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MatchingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.activity_matching_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
    }
}