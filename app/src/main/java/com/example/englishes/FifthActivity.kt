package com.example.englishes

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
/*
Activityの始め方
①class 「アクティビティ名」:AppCompatActivity(){}
② ①のタグの中にoverride fun onCreate(saved~){}
③ ②のタグの中にsuper.onCreate(savedIns~
④ ②のタグの中にenableEdgeToEdge()
⑤ ②のタグの中にsetContentView(「レイアウトファイル名」)
 */

import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import java.util.*

class FifthActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var editText: EditText
    private lateinit var speakButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth)

        editText = findViewById(R.id.daihon)
        val speakButton = findViewById<Button>(R.id.ListenButton)

        speakButton.setOnClickListener {
            val text = editText.text.toString()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    override fun onDestroy() {
        // TTSリソース解放
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
