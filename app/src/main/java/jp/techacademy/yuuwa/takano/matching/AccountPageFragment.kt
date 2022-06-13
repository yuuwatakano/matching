package jp.techacademy.yuuwa.takano.matching

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_fragment.*
import kotlinx.android.synthetic.main.fragment_account_page.*

class AccountPageFragment : Fragment() {

    private lateinit var mDataBaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_page, container, false)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = requireArguments().getString("address")
        val name = requireArguments().getString("name")
        val genre = requireArguments().getString("genre")
        val skill = requireArguments().getString("skill")
        val icon = requireArguments().getByteArray("image")
        val id = requireArguments().getString("id")
        Log.d("tkn5", id.toString())

        if (icon!!.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(icon, 0, icon.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView =iconImageView as ImageView
            imageView.setImageBitmap(image)
        }
        addressText.text = address
        nameText.text = name
        genreText.text = genre
        skillText.text = skill

        match_send_button.setOnClickListener { _ ->
            val database = FirebaseDatabase.getInstance()
            val favoriteref = database.getReference(FavoritePATH)
            val user = FirebaseAuth.getInstance().currentUser
            favoriteref.child(id.toString()).child(user!!.uid).setValue("favuser")
        }
        match_send_cansel_button.setOnClickListener { _ ->
            val database = FirebaseDatabase.getInstance()
            val favoriteref = database.getReference(FavoritePATH)
            val user = FirebaseAuth.getInstance().currentUser
            favoriteref.child(id.toString()).child(user!!.uid).setValue(null)
        }
    }

    override fun onResume() {
        super.onResume()

    }



}