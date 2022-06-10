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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.android.synthetic.main.fragment_all.accountlistView
import kotlinx.android.synthetic.main.item_account_list.*

class AllFragment : Fragment() { private var recyclerView: RecyclerView? = null

    private lateinit var mAccount: Account
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
        return inflater.inflate(R.layout.fragment_all, container, false)

    }

    //Account配列にデータを受け渡すリスナー　ここから
    private val mAccountListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?){
            Log.d("test_account", dataSnapshot.toString())
            Log.d("test_account", dataSnapshot.value.toString())
            val map = dataSnapshot.value as Map<String,String>
            val name = map["name"] ?: ""
            val address = map["address"] ?: ""
            val genre = map["genre"] ?: ""
            val skill = map["skill"] ?: ""
//            val imageString = map["icon"] ?: ""

            Log.d("test_account", name)
            Log.d("test_account", address)
            Log.d("test_account", genre)
            Log.d("test_account", skill)


//            val bytes =
//                if (imageString.isNotEmpty()) {
//                    Base64.decode(imageString, Base64.DEFAULT)
//                } else {
//                    byteArrayOf()
//                }

//
//            //↓このデータをgenerate内のリストにセットしたい
            val account = Account(name, address, genre, skill/*, dataSnapshot.key ?: "",*/)
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

        mAccountRef = mDatabaseReference.child(AccountPATH)
            mAccountRef!!.addChildEventListener(mAccountListener)

//        Log.d("test_account", user.uid.toString())

        this.recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter

//            adapter = AccountViewAdapter(
//                generateItemList(),
//                object : AccountViewAdapter.ListListener {
//                    override fun onClickItem(tappedView: View, itemModel: Account) {
//                        this@AllFragment.onClickItem(tappedView, itemModel)
//                    }
//                }
//            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.recyclerView?.adapter = null
        this.recyclerView = null
    }

//    private fun generateItemList(): List<Account> {
//        val itemList = mutableListOf<Account>()
//        for (i in 0..10) {
//            val item: Account = Account(address = "",genre = "",name = "",skill = ""/*,bytes = ByteArray(1)*/).apply {
//                address = "ad"
//                genre = "gen"
//                name = "name"
//                skill = "skill"
//            }
//            Log.d("test", item.toString())
//            itemList.add(item)
//        }
//        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        recyclerView?.addItemDecoration(itemDecoration)
//        return itemList
//    }

    //RecyclerView内のアイテムがクリックされたときに動く
    private fun onClickItem(tappedView: View, itemModel: Account) {

    }
}