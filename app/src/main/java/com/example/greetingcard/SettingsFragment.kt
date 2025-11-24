// file: com/example/greetingcard/SettingsFragment.kt
package com.example.greetingcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

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
