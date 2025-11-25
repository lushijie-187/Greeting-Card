package com.example.greetingcard.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.greetingcard.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 同理，加载 fragment_settings.xml 布局
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }
}