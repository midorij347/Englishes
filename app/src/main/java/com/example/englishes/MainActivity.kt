package com.example.englishes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*
        ボタンの作り方
        ①findViewByIdでインスタンス化。
        ②インスタンスはボタン型なので、Buttonクラスのメソッドが使える。
        ③setOnClickListenerメソッドを使い、ボタンがクリックされたときにどう動くかを設定。
         */
        val button = findViewById<Button>(R.id.CreateNext)
        button.setOnClickListener(){
            val intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)
        }
        val button1 =findViewById<Button>(R.id.ReflectPast)
        button1.setOnClickListener(){
            val intent =Intent(this,ThirdActivity::class.java)
            startActivity(intent)
        }
        /*
        アクティビティの遷移
        ①val intent = Intent(this,「アクティビティ名」::class.java)
        ②startActivity(intent)
        ③AndroidManifestで、
        <activity android:name=".SecondActivity"
            android:exported="true"/>を忘れずに。
         */
        val ttsbutton= findViewById<Button>(R.id.tts)
        ttsbutton.setOnClickListener(){
            val intent=Intent(this,FifthActivity::class.java)
            startActivity(intent)
        }
    }

}