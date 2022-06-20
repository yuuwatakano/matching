package jp.techacademy.yuuwa.takano.matching

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction

class AccountPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)
        val address =intent.getStringExtra("address")
        val name =intent.getStringExtra("name")
        val profile =intent.getStringExtra("profile")
        val genre =intent.getStringExtra("genre")
        val skill =intent.getStringExtra("skill")
        val id =intent.getStringExtra("id")
        val twitterid =intent.getStringExtra("twitterid")
        val instagramid =intent.getStringExtra("instagramid")
        val soundcloudid =intent.getStringExtra("soundcloudid")
        val icon=intent.getByteArrayExtra("image")
        Log.d("tkn2", address.toString())
        Log.d("tkn2", name.toString())
        Log.d("tkn2", genre.toString())
        Log.d("tkn2", skill.toString())

        val args = Bundle()
        args.putString("address", address)
        args.putString("name", name)
        args.putString("profile", profile)
        args.putString("genre", genre)
        args.putString("skill", skill)
        args.putString("id", id)
        args.putByteArray("image", icon)
        args.putString("twitterid", twitterid)
        args.putString("instagramid", instagramid)
        args.putString("soundcloudid", soundcloudid)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        val fragment = AccountPageFragment()

        fragment.setArguments(args)
        transaction.add(R.id.fl, fragment)
        transaction.commit()
    }
}