package com.example.greetingcard.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.greetingcard.R

class ProfileFragment : Fragment() {

    /**
     * 系统会调用这个方法来创建 Fragment 的视图。
     * 这是 Fragment 显示UI最关键的一步。
     */
    override fun onCreateView(
        inflater: LayoutInflater,       // 用于加载XML布局的加载器
        container: ViewGroup?,          // 该Fragment的UI将被插入到的父ViewGroup
        savedInstanceState: Bundle?
    ): View? {
        // 使用 inflater 将 fragment_profile.xml 布局文件转换成一个 View 对象
        // 第三个参数 false 表示不要立即将这个视图添加到 container 中，因为系统会自动处理。
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view // 返回创建好的视图
    }
}