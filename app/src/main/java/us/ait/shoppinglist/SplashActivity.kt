package us.ait.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.acitivity_splash.*


class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_splash)
        var demoAnim = AnimationUtils.loadAnimation(
            this@SplashActivity,
            R.anim.splash_animation
        )
        ivIcon.startAnimation(demoAnim)
        Handler().postDelayed({
            val intent = Intent(this, ScrollingActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)

    }
}
