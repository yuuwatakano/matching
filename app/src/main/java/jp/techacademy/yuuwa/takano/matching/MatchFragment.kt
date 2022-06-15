package jp.techacademy.yuuwa.takano.matching


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_all.*

class MatchFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_match, container, false)

    }

    //Account配列にデータを受け渡すリスナー　ここから
    private val mAccountListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            Log.d("test_local1", dataSnapshot.toString())
            Log.d("test_local1", dataSnapshot.key.toString())
            Log.d("test_local1", dataSnapshot.value.toString())
            if (dataSnapshot.value == null){
                return
            }

            val matchKey = dataSnapshot.key

            val all = "all"

            mAccountRef = mDatabaseReference.child(AccountPATH).child(all).child(matchKey.toString())

            mAccountRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(Snapshot: DataSnapshot) {
                    val map = Snapshot.value as Map<String,String>
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("test_local1", Snapshot.toString())
                    val name = map["name"] ?: ""
                    val address = map["address"] ?: ""
                    val genre = map["genre"] ?: ""
                    val skill = map["skill"] ?: ""
                    val imageString = map["image"] ?: ""
                    val id = map["id"] ?: ""
                    if (id == user!!.uid){
                        return
                    }
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }
                    Log.d("test_local3", name)
                    Log.d("test_local3", address)
                    Log.d("test_local3", genre)
                    Log.d("test_local3", skill)

                    val account = Account(name, address, genre, skill,id,bytes)
                    Log.d("test", account.toString())
                    mAccountArrayList.add(account)
                    mAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(firebaseError: DatabaseError) {}
            })
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onCancelled(databaseError: DatabaseError) {}
    }


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
                    this@MatchFragment.onClickItem(tappedView, itemModel)
                }
            })
        val all = "all"
        val matching = "matching"
        val user = FirebaseAuth.getInstance().currentUser
        mAccountRef = mDatabaseReference.child(AccountPATH).child(all).child(user!!.uid).child(matching)
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
        startActivity(intent)
    }

}