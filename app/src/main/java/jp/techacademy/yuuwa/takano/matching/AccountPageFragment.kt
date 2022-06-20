package jp.techacademy.yuuwa.takano.matching

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_account_fragment.*
import kotlinx.android.synthetic.main.fragment_account_page.*

class AccountPageFragment : Fragment() {

    private lateinit var mDataBaseReference: DatabaseReference
    private var mCheckRef: DatabaseReference? = null
    private var mMatchRef: DatabaseReference? = null
    private var mMatchingCheckRef: DatabaseReference? = null
    private var isMatched : Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_page, container, false)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        val id = requireArguments().getString("id")
        mDataBaseReference = FirebaseDatabase.getInstance().reference

        //マッチングしている場合はボタンを全て消す
        val matching = "matching"
        val all = "all"
        mMatchingCheckRef = mDataBaseReference.child(AccountPATH).child(all).child(user!!.uid).child(matching).child(id.toString())
        mMatchingCheckRef!!.addListenerForSingleValueEvent(mMatchingCheckListener)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        match_send_button.visibility = View.INVISIBLE
        match_send_cansel_button.visibility = View.INVISIBLE
        match_button.visibility = View.INVISIBLE
        match_delete_button.visibility = View.INVISIBLE
        twitter_button.visibility = View.INVISIBLE
        instagram_button.visibility = View.INVISIBLE
        soundcloud_button.visibility = View.INVISIBLE

        val address = requireArguments().getString("address")
        val name = requireArguments().getString("name")
        val profile = requireArguments().getString("profile")
        val genre = requireArguments().getString("genre")
        val skill = requireArguments().getString("skill")
        val icon = requireArguments().getByteArray("image")
        val id = requireArguments().getString("id")
        val twitterid = requireArguments().getString("twitterid")
        val instagramid = requireArguments().getString("instagramid")
        val soundcloudid = requireArguments().getString("soundcloudid")
        Log.d("tkn5", id.toString())

        if (icon!!.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(icon, 0, icon.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView =iconImageView as ImageView
            imageView.setImageBitmap(image)
        }

        addressText.text = address
        nameText.text = name
        profileText.text = profile
        genreText.text = genre
        skillText.text = skill
        twitterText.text = twitterid
        instagramText.text = instagramid
        soundcloudText.text = soundcloudid

        match_send_button.setOnClickListener { _ ->
            val database = FirebaseDatabase.getInstance()
            val favoriteref = database.getReference(FavoritePATH)
            val user = FirebaseAuth.getInstance().currentUser
            favoriteref.child(id.toString()).child(user!!.uid).setValue("favuser")
            match_send_cansel_button.visibility = View.VISIBLE
            match_send_button.visibility = View.INVISIBLE
        }

        match_send_cansel_button.setOnClickListener { _ ->
            val database = FirebaseDatabase.getInstance()
            val favoriteref = database.getReference(FavoritePATH)
            val user = FirebaseAuth.getInstance().currentUser
            favoriteref.child(id.toString()).child(user!!.uid).setValue(null)
            match_send_button.visibility = View.VISIBLE
            match_send_cansel_button.visibility = View.INVISIBLE
        }

        match_button.setOnClickListener { _ ->
            val database = FirebaseDatabase.getInstance()
            val accountref = database.getReference(AccountPATH)
            val user = FirebaseAuth.getInstance().currentUser
            val all = "all"
            val id = requireArguments().getString("id")
            val matching = "matching"
            accountref.child(all).child(user!!.uid).child(matching).child(id.toString()).setValue("matchinguser")
            accountref.child(all).child(id.toString()).child(matching).child(user!!.uid).setValue("matchinguser")
            val favoriteref = database.getReference(FavoritePATH)
            favoriteref.child(user!!.uid).child(id.toString()).setValue(null)
            match_button.visibility = View.INVISIBLE
            match_delete_button.visibility = View.VISIBLE
        }

        match_delete_button.setOnClickListener { _ ->
            val database = FirebaseDatabase.getInstance()
            val accountref = database.getReference(AccountPATH)
            val user = FirebaseAuth.getInstance().currentUser
            val all = "all"
            val id = requireArguments().getString("id")
            val matching = "matching"
            accountref.child(all).child(user!!.uid).child(matching).child(id.toString()).setValue(null)
            accountref.child(all).child(id.toString()).child(matching).child(user!!.uid).setValue(null)
            match_delete_button.visibility = View.INVISIBLE
            match_send_button.visibility = View.VISIBLE
        }

        twitter_button.setOnClickListener {
            context?.let { it1 -> navigateToTwitterUserPage(twitterid.toString(), it1) }
        }
        instagram_button.setOnClickListener {
            context?.let { it1 -> navigateToInstagramUserPage(instagramid.toString(), it1) }
        }
        soundcloud_button.setOnClickListener{
            context?.let { it1 -> navigateToSoundCloud(soundcloudid.toString(), it1) }
        }







    }
    private val mCheckListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d("test_ck", dataSnapshot.toString())
            Log.d("test_ck", dataSnapshot.key.toString())
            Log.d("test_ck", dataSnapshot.value.toString())
            if (dataSnapshot.value == null){
                match_send_button.visibility = View.VISIBLE
                match_send_cansel_button.visibility = View.INVISIBLE
                match_button.visibility = View.INVISIBLE
            }else{
                match_send_button.visibility = View.INVISIBLE
                match_send_cansel_button.visibility = View.VISIBLE
                match_button.visibility = View.INVISIBLE
            }
            return
        }
        override fun onCancelled(p0: DatabaseError) {}
    }

    private val mMatchListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d("test_match", dataSnapshot.toString())
            Log.d("test_match", dataSnapshot.key.toString())
            Log.d("test_match", dataSnapshot.value.toString())
            val id = requireArguments().getString("id")
            if (dataSnapshot.value != null){
                match_send_button.visibility = View.INVISIBLE
                match_send_cansel_button.visibility = View.INVISIBLE
                match_button.visibility = View.VISIBLE
            }else{
                initUnmatchSend(id.toString())
            }
            return
        }
        override fun onCancelled(p0: DatabaseError) {}
    }

    private val mMatchingCheckListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d("test_ck1", dataSnapshot.toString())
            Log.d("test_ck1", dataSnapshot.key.toString())
            Log.d("test_ck1", dataSnapshot.value.toString())
            val id = requireArguments().getString("id")
            Log.d("test_ck1", id.toString())

            if(dataSnapshot.value == null){
                Log.d("test_ck5", dataSnapshot.key.toString())
                Log.d("test_ck5", id.toString())
                initUnMatch(id.toString())
            }else{
                match_delete_button.visibility = View.VISIBLE
                twitter_button.visibility = View.VISIBLE
                instagram_button.visibility = View.VISIBLE
                soundcloud_button.visibility = View.VISIBLE
            }

            return
        }
        override fun onCancelled(p0: DatabaseError) {}
    }

    private fun initUnMatch(unMatchUserId:String){
        val user = FirebaseAuth.getInstance().currentUser

        //マッチング申請”されているか”チェック[申請されている場合はマッチング承諾ボタン]
        mMatchRef =
            mDataBaseReference.child(FavoritePATH).child(user!!.uid).child(unMatchUserId)
        mMatchRef!!.addListenerForSingleValueEvent(mMatchListener)
    }

    private fun initUnmatchSend(unMatchUserSendId:String){
        val user = FirebaseAuth.getInstance().currentUser

        //マッチング申請しているかチェック[申請済みは申請キャンセルボタン/申請していなければ申請ボタン]
        mCheckRef =
            mDataBaseReference.child(FavoritePATH).child(unMatchUserSendId).child(user!!.uid)
        Log.d("test_ck6", unMatchUserSendId)
        Log.d("test_ck5", user!!.uid)
        Log.d("test_ck5", FavoritePATH)
        mCheckRef!!.addListenerForSingleValueEvent(mCheckListener)
    }




    fun navigateToSoundCloud(userId: String, context: Context) {
        val url = "https://soundcloud.com/$userId"
        try {
            Intent(Intent.ACTION_VIEW).also {
                it.setPackage("com.soundcloud.android")
                it.data = Uri.parse(url)
                context.startActivity(it)
            }
        } catch (e: ActivityNotFoundException) {
            navigateToCustomTab(url, context)
        }
    }

    //Instagram
    fun navigateToInstagramUserPage(userId: String, context: Context) {
        val url = "http://instagram.com/$userId"
        try {
            Intent(Intent.ACTION_VIEW).also {
                it.setPackage("com.instagram.android")
                it.data = Uri.parse(url)
                context.startActivity(it)
            }
        } catch (e: ActivityNotFoundException) {
            navigateToCustomTab(url, context)
        }
    }

    //Twitter
    fun navigateToTwitterUserPage(userId: String, context: Context) {
        val url = "https://twitter.com/$userId"
        try {
            Intent(Intent.ACTION_VIEW).also {
                it.setPackage("com.twitter.android")
                it.data = Uri.parse(url)
                context.startActivity(it)
            }
        } catch (e: ActivityNotFoundException) {
            navigateToCustomTab(url, context)
        }
    }

    //Custom Tab
    fun navigateToCustomTab(url: String, context: Context) {
        val uri = Uri.parse(url)
        CustomTabsIntent.Builder().also { builder ->
            builder.setShowTitle(true)
            builder.build().also {
                it.launchUrl(context, uri)
            }
        }
    }

}