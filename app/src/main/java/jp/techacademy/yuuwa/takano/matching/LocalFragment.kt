package jp.techacademy.yuuwa.takano.matching


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_account_page.*
import kotlinx.android.synthetic.main.fragment_all.*

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
    private val mAccountListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.d("test_local", dataSnapshot.toString())
            Log.d("test_local", dataSnapshot.key.toString())
            Log.d("test_local", dataSnapshot.value.toString())
            val map = dataSnapshot.value as Map<*, *>
            val ad = map["address"]?: ""
            val gr = map["genre"]?: ""
            val Local = "Local"
            Log.d("test_local", ad.toString())
            Log.d("test_local", gr.toString())


            mAccountRef = mDatabaseReference.child(AccountPATH).child(Local).child(ad.toString()).child(gr.toString())

            mAccountRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as Map<String,String>
                    Log.d("test_local1", snapshot.toString())
                    val name = map["name"] ?: ""
                    val address = map["address"] ?: ""
                    val genre = map["genre"] ?: ""
                    val skill = map["skill"] ?: ""
                    val imageString = map["image"] ?: ""
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }
                    Log.d("test_local1", name)
                    Log.d("test_local1", address)
                    Log.d("test_local1", genre)
                    Log.d("test_local1", skill)

                    val account = Account(name, address, genre, skill,bytes)
                    Log.d("test", account.toString())
                    mAccountArrayList.add(account)
                    mAdapter.notifyDataSetChanged()

                }
                override fun onCancelled(firebaseError: DatabaseError) {}
            })
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
                    this@LocalFragment.onClickItem(tappedView, itemModel)
                }
            })
        val user = FirebaseAuth.getInstance().currentUser
        val all = "all"
        if(user != null){
            mAccountRef = mDatabaseReference.child(AccountPATH).child(all)//.child(user!!.uid)
            mAccountRef!!.addChildEventListener(mAccountListener)
        }else{
            startActivity(Intent(context, LoginActivity::class.java))
        }


        this.recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
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

    }
}