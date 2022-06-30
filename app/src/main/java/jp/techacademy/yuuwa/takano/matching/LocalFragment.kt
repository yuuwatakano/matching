package jp.techacademy.yuuwa.takano.matching


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_account_page.*
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.android.synthetic.main.fragment_all.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_local.*

class LocalFragment : Fragment() {
    private var recyclerView: RecyclerView? = null

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mAccountArrayList: ArrayList<Account>
    private lateinit var mAdapter: AccountViewAdapter
    private var mAccountRef: DatabaseReference? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    //Account配列にデータを受け渡すリスナー　ここから
    private val mAccountListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.value == null) {
                return
            }

            val map = dataSnapshot.value as Map<*, *>
            val ad = map["address"] ?: ""
            val gr = map["genre"] ?: ""
            val Local = "Local"
            mAccountRef = mDatabaseReference.child(AccountPATH).child(Local).child(ad.toString())
                .child(gr.toString())

            mAccountRef!!.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(Snapshot: DataSnapshot, s: String?) {
                    val map = Snapshot.value as Map<String, String>
                    val user = FirebaseAuth.getInstance().currentUser
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
                    if (id == user!!.uid) {
                        return
                    }
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }
                    val account = Account(
                        name,
                        profile,
                        address,
                        genre,
                        skill,
                        id,
                        twitterID,
                        instagram,
                        soundcloud,
                        bytes
                    )
                    mAccountArrayList.add(account)
                    mAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(firebaseError: DatabaseError) {}
                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            })
        }

        override fun onCancelled(p0: DatabaseError) {}
    }
    //Account配列にデータを受け渡すリスナー　ここまで


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.recyclerView = view.findViewById(R.id.accountlistView)
        swipeRefreshLayout.setOnRefreshListener {
            onResume()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        local_progress_bar.visibility = View.VISIBLE
        mAccountArrayList = ArrayList<Account>()
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        mAdapter = AccountViewAdapter(mAccountArrayList,
            object : AccountViewAdapter.ListListener {
                override fun onClickItem(tappedView: View, itemModel: Account) {
                    this@LocalFragment.onClickItem(tappedView, itemModel)
                }
            })
        val user = FirebaseAuth.getInstance().currentUser
        val all = "all"
        if (user != null) {
            mAccountRef = mDatabaseReference.child(AccountPATH).child(all).child(user!!.uid)
            mAccountRef!!.addListenerForSingleValueEvent(mAccountListener)
        } else {
            startActivity(Intent(context, LoginActivity::class.java))
        }
        this.recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        local_progress_bar.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.recyclerView?.adapter = null
        this.recyclerView = null
    }

    //RecyclerView内のアイテムがクリックされたときに動く
    private fun onClickItem(tappedView: View, itemModel: Account) {
        val intent = Intent(activity, AccountPageActivity::class.java)
        intent.putExtra("address", itemModel.address)
        intent.putExtra("name", itemModel.name)
        intent.putExtra("profile", itemModel.profile)
        intent.putExtra("genre", itemModel.genre)
        intent.putExtra("skill", itemModel.skill)
        intent.putExtra("id", itemModel.id)
        intent.putExtra("image", itemModel.imageBytes)
        intent.putExtra("twitterid", itemModel.twitterid)
        intent.putExtra("instagramid", itemModel.instagramid)
        intent.putExtra("soundcloudid", itemModel.soundcloudid)
        startActivity(intent)
    }

}
