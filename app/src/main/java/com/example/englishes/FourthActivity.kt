package com.example.englishes

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat

class FourthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        val colorizedText = intent.getStringExtra("colorized_text") ?: ""

        val textView = findViewById<TextView>(R.id.colorizedResult)
        textView.text = HtmlCompat.fromHtml(colorizedText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // 長文でもスクロールできるようにする（安全策）
        textView.movementMethod = ScrollingMovementMethod()
    }
}
