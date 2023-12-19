package com.example.movie_test.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Instrumentation.ActivityResult
import android.content.ContentProviderClient
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.movie_test.R
import com.example.movie_test.databinding.ActivityMainBinding
import com.example.movie_test.model.Movie
import com.example.movie_test.model.MovieDbClient
import com.example.movie_test.ui.detail.DetailActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DEFAULT_REGION = "US"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val moviesAdapter = MoviesAdapter(emptyList()) { navigateTo(it) }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            val message = if (isGranted) "Permission Granted" else "Permission Rejected"
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            /*val message = when {
                isGranted -> "Permission Granted"
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> "Should show Rational"
                else -> "Permission Denied"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()*/
            doRequestPopularMovies(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // binding.showButton.setOnClickListener {
       //     val message = binding.message.text
       //     Toast.makeText(this, message, Toast.LENGTH_LONG).show()
       // }  Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.recycler.adapter = moviesAdapter

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)


        /**thread {
            val apiKey = getString(R.string.api_key)
            val popularMovies = MovieDbClient.service.listPopularMovies(apiKey)
            val body = popularMovies.execute().body()

            runOnUiThread {
                if (body != null)
                    moviesAdapter.movies = body.results
                    moviesAdapter.notifyDataSetChanged()

            //Log.d("MainActivity", "Movie count: ${body.results.size}")
            }

        }**/
    }

    private fun doRequestPopularMovies(isLocationGranted: Boolean) {
        lifecycleScope.launch {
            val apiKey = getString(R.string.api_key)
            val region = getRegion(isLocationGranted)
            val popularMovies = MovieDbClient.service.listPopularMovies(apiKey, region)
            moviesAdapter.movies = popularMovies.results
            moviesAdapter.notifyDataSetChanged()
        }
    }
    @SuppressLint("MissingPermission")
    private suspend fun getRegion(isLocationGranted: Boolean): String = suspendCancellableCoroutine { continuation ->
        if (isLocationGranted) {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                continuation.resume(getRegionFromLocation(it.result))
//                Toast.makeText(this, "itRes: "+(it.result).toString() , Toast.LENGTH_LONG).show()
            }
        } else {
            continuation.resume(DEFAULT_REGION)
        }
    }

    private fun getRegionFromLocation(location: Location?): String {
        if (location == null) {
            return DEFAULT_REGION
        }
        val geocoder = Geocoder(this)
        val result = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )
        return result?.firstOrNull()?.countryCode ?: DEFAULT_REGION
    }

    private fun navigateTo(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)

        startActivity(intent)
    }
}