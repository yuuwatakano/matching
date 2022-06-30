package jp.techacademy.yuuwa.takano.matching

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_account_fragment.*


class AccountFragment : Fragment() {
    private lateinit var mDatabaseReference: DatabaseReference
    private var mAccountRef: DatabaseReference? = null
    private var settingFrag: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_account_fragment, container, false)
    }

    private val mAccountListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {//子データがある際はデータを引き出す
            Log.d("test_account", dataSnapshot.toString())
            Log.d("test_account", dataSnapshot.value.toString())
            val map = dataSnapshot.value as Map<String, String>

            val name = map["name"] ?: ""
            val profile = map["profile"] ?: ""
            val address = map["address"] ?: ""
            val genre = map["genre"] ?: ""
            val skill = map["skill"] ?: ""
            val imageString = map["image"] ?: ""
            val id = map["id"] ?: ""
            val twitterID = map["twitter"] ?: ""
            val instagram = map["instagram"] ?: ""
            val soundcloud = map["soundcloud"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }
            val user = FirebaseAuth.getInstance().currentUser
            if (user!!.uid != id) {
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
            if (settingFrag) {
                val intent = Intent(context, AccountChange::class.java)
                intent.putExtra("loginaddress", address.toString())
                intent.putExtra("loginname", name.toString())
                intent.putExtra("loginprofile", profile.toString())
                intent.putExtra("logingenre", genre.toString())
                intent.putExtra("loginskill", skill.toString())
                intent.putExtra("loginid", id.toString())
                intent.putExtra("loginimage", bytes)
                intent.putExtra("logintwitterid", twitterID.toString())
                intent.putExtra("logininstagramid", instagram.toString())
                intent.putExtra("loginsoundcloudid", soundcloud.toString())
                settingFrag = false
                startActivity(intent)
            }
            Log.d("tkn8", address.toString())
            Log.d("tkn8", name.toString())
            Log.d("tkn8", genre.toString())
            Log.d("tkn8", skill.toString())
            Log.d("tkn8", id.toString())
            Log.d("tkn8", bytes.toString())
            Log.d("tkn8", twitterID.toString())
            Log.d("tkn8", instagram.toString())
            Log.d("tkn8", soundcloud.toString())
            account_address1.text = address
            account_name.text = name
            account_profile.text = profile
            account_genre.text = genre
            account_skill.text = skill
            account_twitter.text = twitterID
            account_instagram.text = instagram
            account_soundcloud.text = soundcloud
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
                val imageView = icon_imageView as ImageView
                imageView.setImageBitmap(image)
            } else {
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

    override fun onResume() {//ログインアカウントのデータ取得
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(context, LoginActivity::class.java))
        }
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        val all = "all"
        mAccountRef = mDatabaseReference.child(AccountPATH).child(all)
        mAccountRef!!.addChildEventListener(mAccountListener)

        if (user == null) {
            login.visibility = View.VISIBLE
            logout.visibility = View.INVISIBLE
        } else {
            login.visibility = View.INVISIBLE
            logout.visibility = View.VISIBLE
            val user = FirebaseAuth.getInstance().currentUser
            Log.v("test_user", "ログインした ID:" + user?.uid!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login.setOnClickListener {//ログインボタン
            startActivity(Intent(context, LoginActivity::class.java))
        }
        logout.setOnClickListener { _ ->
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "ログアウトしました", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, LoginActivity::class.java))
        }
        accountChange.setOnClickListener {//アカウント設定ボタン
            settingFrag = true
            val user = FirebaseAuth.getInstance().currentUser
            Log.v("test_user1", "ログインした ID:" + user?.uid!!)
            mDatabaseReference = FirebaseDatabase.getInstance().reference
            val all = "all"
            mAccountRef = mDatabaseReference.child(AccountPATH).child(all)
            mAccountRef!!.addChildEventListener(mAccountListener)
        }
        qa.setOnClickListener {//Q&Aボタン
            startActivity(Intent(context, QaActivity::class.java))

        }
    }
}

