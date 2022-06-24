package jp.techacademy.yuuwa.takano.matching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_qa.*

class QaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent_status_bar)
        back_buttom1.setOnClickListener {
            finish()
        }
    }
}