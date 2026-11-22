package com.example.weatherapp

data class WeatherResponse(
    val name: String,
    val weather: List<WeatherInfo>
)

data class WeatherInfo(
    val main: String,
    val description: String,
    val icon: String
)