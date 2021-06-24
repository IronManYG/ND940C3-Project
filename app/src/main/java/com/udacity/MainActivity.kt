package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var isChecked = false

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    // Notification ID.
    private val NOTIFICATION_ID = 0


    private var description = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if(!isChecked){
                custom_button.checked(isChecked)
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
            } else{
                custom_button.checked(isChecked)
                ButtonState.Loading.status = "Progress"
                download()
            }
        }

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    ButtonState.Completed.status = "Success"
                    custom_button.hasCompletedDownload()
                    startNotification()
                }
                if (DownloadManager.STATUS_FAILED == status) {
                    ButtonState.Completed.status = "Fail"
                    custom_button.hasCompletedDownload()
                    startNotification()
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {

        var mTitle = ""

        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.glide_radioButton ->
                    if (checked) {
                        isChecked = true
                        URL = "https://github.com/bumptech/glide"
                        mTitle = getString(R.string.glide_radio_title)
                        ButtonState.Completed.fieName = mTitle
                        description = getString(R.string.notification_glide_description) + " " + ButtonState.Completed.status
                    }
                R.id.loadApp_radioButton ->
                    if (checked) {
                        isChecked = true
                        URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                        mTitle = getString(R.string.loadApp_radio_title)
                        ButtonState.Completed.fieName = mTitle
                        description = getString(R.string.notification_project_3_description) + " " + ButtonState.Completed.status
                    }
                R.id.retrofit_radioButton ->
                    if (checked) {
                        isChecked = true
                        URL = "https://github.com/square/retrofit"
                        mTitle = getString(R.string.retrofit_radio_title)
                        ButtonState.Completed.fieName = mTitle
                        description = getString(R.string.notification_retrofit_description) + " " + ButtonState.Completed.status
                    }
            }
        }
    }

    private fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
        // Create the content intent for the notification, which launches
        // DetailActivity
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)

        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_notification_channel_id)
        )

            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(mTitle)
            .setContentText(messageBody)

            .setAutoCancel(true)

            .addAction(
                R.drawable.ic_assistant_black_24dp,
                "Check the status",
                pendingIntent
            )


            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notify(NOTIFICATION_ID, builder.build())

    }

    //Cancel all notifications
    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }


    private fun createChannel(channelId: String, channelName: String) {
        //START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                //disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_notification_channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun startNotification() {
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            description,
            applicationContext
        )
    }

}
