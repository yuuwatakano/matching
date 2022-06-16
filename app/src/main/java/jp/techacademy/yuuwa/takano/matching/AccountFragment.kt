package jp.techacademy.yuuwa.takano.matching

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_account_fragment.*
import kotlinx.android.synthetic.main.activity_account_page.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream

class AccountFragment : Fragment() {
    private lateinit var mDatabaseReference: DatabaseReference
    private var mAccountRef: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_account_fragment, container, false)
    }

    private val mAccountListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.d("test_account", dataSnapshot.toString())
            Log.d("test_account", dataSnapshot.value.toString())
            if (dataSnapshot.value == null){
                return
            }

            val map = dataSnapshot.value as Map<String, String>

            var name = map["name"] ?: ""
            var address = map["address"] ?: ""
            var genre = map["genre"] ?: ""
            var skill = map["skill"] ?: ""
            var imageString = map["image"] ?: ""
            var id = map["id"] ?: ""
            var twitterID = map["twitter"] ?: ""
            var instagram = map["instagram"] ?: ""
            var soundcloud = map["soundcloud"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }
            val user = FirebaseAuth.getInstance().currentUser
            if(user!!.uid != id){
                return
            }
            Log.d("test_account1", name)
            Log.d("test_account1", address)
            Log.d("test_account1", genre)
            Log.d("test_account1", skill)
            Log.d("test_account3", imageString)
            Log.d("test_account1", id)
            Log.d("test_account1", twitterID)
            Log.d("test_account1", instagram)
            Log.d("test_account1", soundcloud)

            account_address.text = address
            account_name.text = name
            account_genre.text = genre
            account_skill.text = skill
            account_twitter.text = twitterID
            account_instagram.text = instagram
            account_soundcloud.text = soundcloud
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
                val imageView = icon_imageView as ImageView
                imageView.setImageBitmap(image)
            }else{
                val imageView = icon_imageView as ImageView
                imageView.setImageBitmap(null)
            }
            return
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onCancelled(p0: DatabaseError) {}
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        val all = "all"
        mAccountRef = mDatabaseReference.child(AccountPATH).child(all)
        mAccountRef!!.addChildEventListener(mAccountListener)

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
