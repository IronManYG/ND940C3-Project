package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancel(0)

        file_name_content.text = ButtonState.Completed.fieName

        if(ButtonState.Completed.status == "Success") {
            status_content.setTextColor(getColor(android.R.color.holo_green_dark))
        } else if (ButtonState.Completed.status == "Fail"){
            status_content.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        status_content.text = ButtonState.Completed.status

        ok_button.setOnClickListener {
            finish()
        }

    }

}
