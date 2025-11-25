package com.example.greetingcard.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.greetingcard.ui.profile.ProfileFragment
import com.example.greetingcard.ui.settings.SettingsFragment

/**
 * 这个 Adapter 负责为 ViewPager2 提供 Fragment 页面。
 *
 * @param fragmentActivity 宿主 Activity，通常就是 this。
 */
class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    // 定义要展示的 Fragment 列表
    private val fragments = listOf(
        HomeFragment(),
        ProfileFragment(),
        SettingsFragment()
    )

    /**
     * 返回 Fragment 的总数。
     */
    override fun getItemCount(): Int {
        return fragments.size
    }

    /**
     * 根据位置 position 创建并返回对应的 Fragment 实例。
     * ViewPager2 会在需要展示某个页面时调用这个方法。
     */
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}