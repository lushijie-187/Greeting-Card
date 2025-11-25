package com.example.greetingcard.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.greetingcard.util.LoginManager
import com.example.greetingcard.R
import com.example.greetingcard.ui.widget.ZoomOutPageTransformer
import com.example.greetingcard.ui.auth.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. 初始化视图
        viewPager = findViewById(R.id.view_pager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // 2. 设置 ViewPager2 的 Adapter
        pagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.setPageTransformer(ZoomOutPageTransformer())

        // --- 联动逻辑开始 ---

        // 3. 设置 BottomNavigationView 的点击监听
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // 点击"首页"，让 ViewPager 切换到第 0 页
                    viewPager.setCurrentItem(0, true) // true 表示平滑滚动
                    true
                }

                R.id.nav_profile -> {
                    // 点击"我的"，让 ViewPager 切换到第 1 页
                    viewPager.setCurrentItem(1, true)
                    true
                }

                R.id.nav_settings -> {
                    // 点击"设置"，让 ViewPager 切换到第 2 页
                    viewPager.setCurrentItem(2, true)
                    true
                }

                R.id.nav_logout -> {
                    logout()
                    false
                }

                else -> false
            }
        }

        // 4. 设置 ViewPager2 的页面切换回调
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 当 ViewPager 滑动到某一页时，更新 BottomNavigationView 的选中项
                // 注意：要检查 bottomNavigationView 的选中项是否已经是目标项，避免循环调用
                val targetItemId = when (position) {
                    0 -> R.id.nav_home
                    1 -> R.id.nav_profile
                    2 -> R.id.nav_settings
                    else -> R.id.nav_home // 默认
                }
                if (bottomNavigationView.selectedItemId != targetItemId) {
                    bottomNavigationView.selectedItemId = targetItemId
                }
            }
        })

        // 可选：禁用 ViewPager2 的用户滑动输入
        // 如果你只希望通过 BottomNavigationView 来切换页面，可以取消下面的注释
        // viewPager.isUserInputEnabled = false
    }

    // `logout` 方法保持不变
    private fun logout() {
        LoginManager.setLoggedIn(this, false)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}