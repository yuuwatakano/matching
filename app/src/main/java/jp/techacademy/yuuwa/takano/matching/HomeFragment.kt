package jp.techacademy.yuuwa.takano.matching

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_home_fragment.*
import layout.PageAdapter

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        Log.v("test_takano", "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_home_fragment, container, false)

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("test_takano", "onViewCreated")
        //ここの「pager」はfragment_second.xmlのViewPagerのidの事！
        pager.adapter = PageAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(pager)
    }
}

