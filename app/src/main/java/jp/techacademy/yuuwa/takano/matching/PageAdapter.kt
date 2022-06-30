package layout

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import jp.techacademy.yuuwa.takano.matching.AccountPageFragment
import jp.techacademy.yuuwa.takano.matching.AllFragment
import jp.techacademy.yuuwa.takano.matching.LocalFragment
import jp.techacademy.yuuwa.takano.matching.MatchFragment

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        Log.v("test_takano", "getItem:" + position.toString())
        when (position) {
            // どのFragmentを表示するか
            0 -> {
                return LocalFragment()
            }
            1 -> {
                return AllFragment()
            }
            2 -> {
                return MatchFragment()
            }
            else -> {
                return LocalFragment()
            }
        }
    }

    // スワイプビューの数が３つだから
    override fun getCount(): Int {
        return 3;
    }

    // スワイプビューのタイトルを決める
    override fun getPageTitle(position: Int): CharSequence? {
        Log.v("test_takano", "getPageTitle:" + position.toString())
        when (position) {
            0 -> {
                return "Local"
            }
            1 -> {
                return "All"
            }
            2 -> {
                return "Matching"
            }
            else -> {
                return null
            }
        }
    }
}