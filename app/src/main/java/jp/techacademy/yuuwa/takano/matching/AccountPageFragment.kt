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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_page, container, false)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val id = requireArguments().getString("id")
        super.onCreate(savedInstanceState)
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        mCheckRef = mDataBaseReference.child(FavoritePATH).child(id.toString()).child(user!!.uid)
        mCheckRef!!.addListenerForSingleValueEvent(mCheckListener)
        mMatchRef = mDataBaseReference.child(FavoritePATH).child(user!!.uid).child(id.toString())
        mMatchRef!!.addListenerForSingleValueEvent(mMatchListener)


    }





    override fun onResume() {
        super.onResume()


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

        }
    }


    private val mMatchListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d("test_match", dataSnapshot.toString())
            Log.d("test_match", dataSnapshot.key.toString())
            Log.d("test_match", dataSnapshot.value.toString())
            if (dataSnapshot.value != null){
                match_send_button.visibility = View.INVISIBLE
                match_send_cansel_button.visibility = View.INVISIBLE
                match_button.visibility = View.VISIBLE
            }
            return
        }
        override fun onCancelled(p0: DatabaseError) {}
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
                match_send_cansel_button.visibility = View.VISIBLE
                match_send_button.visibility = View.INVISIBLE
                match_button.visibility = View.INVISIBLE
            }
            return
        }
        override fun onCancelled(p0: DatabaseError) {}
    }


}