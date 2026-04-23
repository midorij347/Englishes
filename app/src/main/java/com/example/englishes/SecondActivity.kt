package com.example.englishes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

class SecondActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val client = OkHttpClient()
    private val apiKey = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val newSentence = findViewById<EditText>(R.id.CopyAndPaste)
        val button4 = findViewById<Button>(R.id.translateSentense)

        button4.setOnClickListener {
            val inputText = newSentence.text.toString()

            sendChatGPTRequest(inputText) { result ->
                runOnUiThread {
                    println("API結果: $result")

                
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "app-database"
                    ).build()
                    val dao = db.sentenceDao()

                    lifecycleScope.launch {
                        dao.insert(Sentence(parsedHtml = result))
                    }

                    // FourthActivityに遷移
                    val intent = Intent(this, FourthActivity::class.java)
                    intent.putExtra("colorized_text", result)
                    startActivity(intent)
                }
            }
        }

        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("TTS: この言語はサポートされていません")
            } else {
                println("TTS: 初期化成功、英語設定完了")
            }
        } else {
            println("TTS: 初期化失敗")
        }
    }

    private fun sendChatGPTRequest(userInput: String, callback: (String) -> Unit) {
        val url = "https://api.openai.com/v1/chat/completions"

        val messages = JSONArray().apply {
            put(
                JSONObject().put("role", "system").put("content",
                    "あなたは英語構文解析アシスタントです。以下の英文を、指定された構文ルールに従って解析し、HTML形式で色分けしてください。\n\n" +
                            "【ルール】\n" +
                            "- 指定された構文にマッチする部分のみを色分け対象とします。\n" +
                            "- 複数の構文に該当する場合は、優先順位の数字が小さい構文を優先適用してください。\n" +
                            "- 同じ構文カテゴリが複数回出現する場合は、常に同じ色で統一してください。\n" +
                            "- 構文に該当しない語句には絶対に色を付けないこと。\n\n" +
                            "【出力形式】\n" +
                            "- 出力は HTML の <span style=\"...\">...</span> タグで装飾してください。\n" +
                            "- 文の構造がわかるよう、必要に応じて改行を使用して構いません。\n" +
                            "- 和訳、注釈、説明、挨拶などは一切不要です。\n" +
                            "- 出力は HTML 本文のみとし、構文名やルール名は含めないでください。\n\n" +
                            "【背景前提】\n" +
                            "- UIの背景色は #FFF8DC（ベージュ）です。\n" +
                            "- 色指定はこの背景色と調和する、目に優しく可読性の高い配色を使用してください。\n\n" +
                            "【構文と色分けルール（優先順位順）】\n" +
                            "1: 接続詞 → <span style=\"color:#008000\">...</span>\n" +
                            "2: 省略構文 → <span style=\"background-color:#E0FFFF\">...</span>\n" +
                            "3: so / such構文 → <span style=\"background-color:#FFF0F5\">...</span>\n" +
                            "4: 前置詞＋名詞句 → <span style=\"background-color:#FFFFCC\">...</span>\n" +
                            "5: 疑問詞構文 → <span style=\"background-color:#F5DEB3\">...</span>\n" +
                            "6: 動名詞 / 不定詞 → <span style=\"background-color:#E6F0FF\">...</span>\n" +
                            "7: 名詞構文 → <span style=\"background-color:#FFFACD\">...</span>\n" +
                            "8: 倒置構文 → <span style=\"background-color:#D8BFD8\">...</span>\n" +
                            "9: 強調構文 → <span style=\"background-color:#FFE4B5\">...</span>\n" +
                            "10: 否定構文 → <span style=\"background-color:#FFDDDD\">...</span>\n" +
                            "11: so that構文（目的） → <span style=\"background-color:#ADD8E6\">...</span>\n" +
                            "12: 長い主語 → <span style=\"background-color:#FFD1DC\">...</span>\n" +
                            "13: that節（名詞節） → <span style=\"background-color:#FFCC99\">...</span>\n" +
                            "14: 関係代名詞節 → <span style=\"background-color:#B2E2B2\">...</span>\n" +
                            "15: 関係副詞節 → <span style=\"background-color:#DAF7A6\">...</span>\n" +
                            "16: 仮定法 → <span style=\"background-color:#D3D3D3\">...</span>\n" +
                            "17: 分詞構文 → <span style=\"background-color:#E6E6FA\">...</span>\n" +
                            "18: and（並列構造） → <span style=\"color:#FF0000\">and</span>\n" +
                            "19: 比較構文 → <span style=\"background-color:#F0FFF0\">...</span>\n"
                )
            )
            put(JSONObject().put("role", "user").put("content", userInput))
        }


        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", messages)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback("エラー: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!it.isSuccessful) {
                            callback("エラー: ${response.code}")
                        } else {
                            val responseBody = it.body?.string()
                            val messageContent = JSONObject(responseBody)
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            callback(messageContent)
                        }
                    }
                }
            })
        }
    }
}
