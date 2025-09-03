package com.example.momapp

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        Log.d("PageAdapter", "Creating fragment at position: $position")
        return when (position) {
            0 -> HomeFragment()
            1 -> CareFragment()
            2 -> FamilyFragment()
            3 -> CommunityFragment()
            4 -> SettingFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
