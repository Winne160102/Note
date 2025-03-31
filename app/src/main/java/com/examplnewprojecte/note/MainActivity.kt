package com.examplnewprojecte.note
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.examplnewprojecte.note.R
import com.examplnewprojecte.note.databinding.ActivityMainBinding
import com.examplnewprojecte.note.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "App đã chạy đến onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
}
