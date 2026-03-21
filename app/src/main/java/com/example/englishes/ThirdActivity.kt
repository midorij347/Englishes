package com.example.englishes

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import kotlinx.coroutines.launch

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
        val dao = db.sentenceDao()
        val textView = findViewById<TextView>(R.id.sentenceList)
        textView.movementMethod = ScrollingMovementMethod()

        lifecycleScope.launch {
            val sentences = dao.getAll()
            val joined = sentences.joinToString("<br><br>") { it.parsedHtml }
            textView.text = HtmlCompat.fromHtml(joined, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }
}

@Entity(tableName = "sentences")
data class Sentence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parsedHtml: String
)

@Dao
interface SentenceDao {
    @Insert suspend fun insert(sentence: Sentence)
    @Query("SELECT * FROM sentences") suspend fun getAll(): List<Sentence>
}

@Database(entities = [Sentence::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sentenceDao(): SentenceDao
}
