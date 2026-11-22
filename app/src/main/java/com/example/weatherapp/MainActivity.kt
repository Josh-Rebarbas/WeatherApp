package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val apiKey = "fb6f491378248fdd3c2fcf7ab6469a48"
    private val city = "New York"

    private lateinit var progress: ProgressBar
    private lateinit var weatherContainer: View
    private lateinit var errorContainer: View
    private lateinit var errorText: TextView
    private lateinit var cityText: TextView
    private lateinit var mainText: TextView
    private lateinit var descText: TextView
    private lateinit var iconView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress)
        weatherContainer = findViewById(R.id.weatherContainer)
        errorContainer = findViewById(R.id.errorContainer)
        errorText = findViewById(R.id.errorText)
        cityText = findViewById(R.id.cityText)
        mainText = findViewById(R.id.mainText)
        descText = findViewById(R.id.descText)
        iconView = findViewById(R.id.iconView)
        findViewById<Button>(R.id.retryButton).setOnClickListener { loadWeather() }

        val toolbar = findViewById<TextView>(R.id.toolbar)
        val pad = (16 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbar.setPadding(pad, bars.top + pad, pad, pad)
            v.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        loadWeather()
    }

    private fun loadWeather() {
        progress.visibility = View.VISIBLE
        weatherContainer.visibility = View.GONE
        errorContainer.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(city, apiKey)
                val info = response.weather.firstOrNull()
                if (info == null) {
                    showError("Walay weather data nga nadawat.")
                    return@launch
                }
                cityText.text = response.name
                mainText.text = info.main
                descText.text = info.description
                Glide.with(this@MainActivity)
                    .load("https://openweathermap.org/img/wn/${info.icon}@2x.png")
                    .into(iconView)

                progress.visibility = View.GONE
                weatherContainer.visibility = View.VISIBLE
            } catch (e: HttpException) {
                val msg = if (e.code() == 401) {
                    "Sayop o wala pa active ang API key (401)."
                } else {
                    "Server error (${e.code()})."
                }
                showError(msg)
            } catch (e: IOException) {
                showError("Walay internet connection.")
            } catch (e: Exception) {
                showError("Naay error: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        progress.visibility = View.GONE
        weatherContainer.visibility = View.GONE
        errorText.text = message
        errorContainer.visibility = View.VISIBLE
    }
}