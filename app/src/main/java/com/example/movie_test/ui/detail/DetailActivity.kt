package com.example.movie_test.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.bumptech.glide.Glide
import com.example.movie_test.R
import com.example.movie_test.databinding.ActivityDetailBinding
import com.example.movie_test.model.Movie


class DetailActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_MOVIE = "DetailActivity:title"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(this.layoutInflater)
        this.setContentView(binding.root)

        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        val movie = intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
        if (movie != null) {
//            binding.title.text = movie.title
            title = movie.title
            Glide
                .with(this)
                .load("https://image.tmdb.org/t/p/w780/${movie.backdrop_path}")
                .into(binding.backdrop)
            binding.sumary.text = movie.overview
            bindDetailInfo(binding.detailInfo, movie)
        }

    }

    private fun bindDetailInfo(detailInfo: TextView, movie: Movie) {
        detailInfo.text = buildSpannedString {
            appendInfo(R.string.original_language, movie.original_language)
            appendInfo(R.string.original_title, movie.original_title)
            appendInfo(R.string.release_date, movie.release_date)
            appendInfo(R.string.popularity, movie.popularity.toString())
            appendInfo(R.string.vote_average, movie.vote_average.toString())
            /*bold { append("Original title: ") }
            appendLine(movie.original_title)

            bold { append("Release date: ") }
            appendLine(movie.release_date)

            bold { append("Popularity: ") }
            appendLine(movie.popularity.toString())

            bold { append("Vote average: ") }
            appendLine(movie.vote_average.toString())*/
        }
    }

    private fun SpannableStringBuilder.appendInfo(stringRes: Int, value: String) {
        bold {
            append(getString(stringRes))
            append(": ")
        }

        appendLine(value)
    }
}