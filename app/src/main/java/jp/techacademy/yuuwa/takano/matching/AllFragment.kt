package jp.techacademy.yuuwa.takano.matching

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home_fragment.*
import kotlinx.android.synthetic.main.fragment_account_page.*
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.android.synthetic.main.item_account_list.*

class AllFragment : Fragment() {
    private var recyclerView: RecyclerView? = null

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mAccountArrayList: ArrayList<Account>
    private lateinit var mAdapter: AccountViewAdapter
    private var mAccountRef: DatabaseReference? = null
    private var isLoading = false
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_all, container, false)

    }

    //Account配列にデータを受け渡すリスナー　ここから
    private val mAccountListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.d("test_account", dataSnapshot.toString())
            Log.d("test_account", dataSnapshot.value.toString())
            if (dataSnapshot.value == null){
                return
            }
            val map = dataSnapshot.value as Map<String, String>
            val name = map["name"] ?: ""
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
            if (id == user!!.uid){
                return
            }
            Log.d("test_account", name)
            Log.d("test_account", address)
            Log.d("test_account", genre)
            Log.d("test_account", skill)
            Log.d("test_account1", imageString)
            Log.d("test_account1", id)
            val account = Account(name, address, genre, skill,id,twitterID,instagram,soundcloud,bytes)
            Log.d("test", account.toString())
            mAccountArrayList.add(account)
            mAdapter.notifyDataSetChanged()
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
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
        mAccountArrayList = ArrayList<Account>()
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        mAdapter = AccountViewAdapter(mAccountArrayList,
            object : AccountViewAdapter.ListListener {
                override fun onClickItem(tappedView: View, itemModel: Account) {
                    this@AllFragment.onClickItem(tappedView, itemModel)
                }
            })
        val all = "all"
        mAccountRef = mDatabaseReference.child(AccountPATH).child(all)
        mAccountRef!!.addChildEventListener(mAccountListener)


        this.recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,1)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        this.recyclerView?.adapter = null
        this.recyclerView = null
    }


    //RecyclerView内のアイテムがクリックされたときに動く
    private fun onClickItem(tappedView: View, itemModel: Account) {
        Log.d("tkn", itemModel.address)
        Log.d("tkn", itemModel.name)
        Log.d("tkn", itemModel.genre)
        Log.d("tkn", itemModel.skill)
        Log.d("tkn4", itemModel.id)
        Log.d("tkn", itemModel.imageBytes.toString())
        val intent = Intent(activity, AccountPageActivity::class.java)
        intent.putExtra("address",itemModel.address)
        intent.putExtra("name",itemModel.name)
        intent.putExtra("genre",itemModel.genre)
        intent.putExtra("skill",itemModel.skill)
        intent.putExtra("id",itemModel.id)
        intent.putExtra("image",itemModel.imageBytes)
        intent.putExtra("twitterid",itemModel.twitterid)
        intent.putExtra("instagramid",itemModel.instagramid)
        intent.putExtra("soundcloudid",itemModel.soundcloudid)
        startActivity(intent)
    }

}