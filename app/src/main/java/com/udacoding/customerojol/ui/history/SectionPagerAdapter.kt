package com.udacoding.customerojol.ui.history

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.udacoding.customerojol.ui.home.HomeFragment
import com.udacoding.customerojol.ui.profile.ProfileFragment

class SectionPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm){


    override fun getItem(position: Int): Fragment {
        var fragment  = Fragment()
        when(position){
            0 -> fragment = HistoryFragment()
            1 -> fragment = ProfileFragment()
        }
        return  fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var nameTabs = ""
        when(position){
            0 -> nameTabs = "Home"
            1 -> nameTabs = "Profile"
        }
        return nameTabs
    }

    override fun getCount(): Int {
        return 2
    }

}