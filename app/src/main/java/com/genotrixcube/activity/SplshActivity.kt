package com.genotrixcube.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.genotrixcube.R

class SplshActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splsh)

        splashTimer()
    }



    private fun splashTimer() {
        try {
            //        coutner for splsh activity
            val countDownTimer = object : CountDownTimer(2300,2300){
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    splashCall()
                }

            }
            countDownTimer.start()



        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun splashCall() {

        val dashboard = Intent(this, CoverActivity::class.java)
        startActivity(dashboard)
        finish()
    }
}