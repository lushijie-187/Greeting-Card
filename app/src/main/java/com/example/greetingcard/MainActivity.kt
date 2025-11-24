package com.example.greetingcard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 确保加载的是我们之前创建的登录页布局
        setContentView(R.layout.activity_main)

        // 1. 找到登录按钮
        val loginButton: Button = findViewById(R.id.login_button)

        // 2. 为按钮设置点击监听器
        loginButton.setOnClickListener {
            // 1. 保存登录状态
            LoginManager.setLoggedIn(this, true)
            // 2. 跳转到主页
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

