package com.example.greetingcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 判断登录状态
        if (LoginManager.isLoggedIn(this)) {
            // 如果已登录，直接跳转到主页
            goToHome()
        } else {
            // 如果未登录，跳转到登录页
            goToLogin()
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // 销毁SplashActivity
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 销毁SplashActivity
    }
}
