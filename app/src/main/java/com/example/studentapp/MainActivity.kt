package com.example.studentapp

import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentapp.database.MaterialDatabase
import com.example.studentapp.database.MaterialsData
import com.example.studentapp.questions.Questions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var vibrator: Vibrator
    private var doubleBackToExitPressedOnce = false
    private val mHandler: Handler? = Handler()
    private val mRunnable =
        Runnable { doubleBackToExitPressedOnce = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.mHandler != null) {
            mHandler.removeCallbacks(mRunnable)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        vibration()
        Toast.makeText(this, "Սեղմեք նորից՝ դուրս գալու համար ;)", Toast.LENGTH_SHORT).show()
        mHandler?.postDelayed(mRunnable, 2000)
    }

    private fun vibration() {
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(
                VibrationEffect
                    .createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
            )
        } else {
            vibrator.vibrate(200)
        }
    }
}
