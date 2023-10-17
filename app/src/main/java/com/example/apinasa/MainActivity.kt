package com.example.apinasa

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler
import okhttp3.Headers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.squareup.picasso.Picasso
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var imageName: TextView
    private lateinit var imageView: ImageView
    private lateinit var imageDescription: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageName = findViewById(R.id.imageName)
        imageDescription = findViewById(R.id.imageDescription)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            loadPictureOfDay(calendar.time)
        }
    }

    private fun loadPictureOfDay(day: Date) {
        val desiredFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = desiredFormat.format(day)

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = "W8vActipgxiG2XqYxoeaNT2omRFhskxxOTjplgqZ" // Replace with your actual API key
        params["date"] = formattedDate

        client.get("https://api.nasa.gov/planetary/apod", params, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, response: String?) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        val gson = Gson()
                        val pictureOfTheDay: PictureOfTheDay = gson.fromJson(response, PictureOfTheDay::class.java)

                        // Set the image of the ImageView
                        val imageUrl = pictureOfTheDay.url
                        if (imageUrl.isNotEmpty()) {
                            Picasso.get().load(imageUrl).into(imageView)
                        }

                        // Set the title to the imageName TextView
                        val title = pictureOfTheDay.title
                        imageName.text = title

                        // Set the explanation to the imageDescription TextView
                        val explanation = pictureOfTheDay.explanation
                        imageDescription.text = explanation
                    } catch (e: JsonSyntaxException) {
                        Log.e("API Call", "Error parsing JSON: ${e.message}")
                    }
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.e("API Call", "Error $statusCode: $errorResponse", throwable)
            }
        })
    }
}