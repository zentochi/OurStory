package com.danuartadev.ourstory.ui.story.detailStory

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.danuartadev.ourstory.data.remote.response.ListStoryItem
import com.danuartadev.ourstory.databinding.ActivityDetailBinding
import com.danuartadev.ourstory.utils.DateFormatter
import com.danuartadev.ourstory.utils.LocationUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listStoryItems = intent.getParcelableArrayListExtra<ListStoryItem>("list_story_item")

        if (!listStoryItems.isNullOrEmpty()) {
            val story = listStoryItems[0]
            binding.tvProfileName.text = story.name
            binding.tvUsernameDesc.text = story.description
            val locationUtils = LocationUtils.getStringAddress(
                LocationUtils.toLatlng(story.lat, story.lon),
                this
            )
            binding.tvLocation.text = locationUtils

//            val isoDate = story.createdAt
//            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//            val parsedDate = isoDate?.let { isoFormat.parse(it) }
//            if (parsedDate != null) {
//                binding.tvUptimeStory.setReferenceTime(parsedDate.time)
//            }

            val storyTime = story.createdAt
            if (storyTime != null) {
                val formattedDate = DateFormatter.formatDate(storyTime, TimeZone.getDefault().id)
                binding.tvUptimeStory.text = formattedDate
            }

             Glide.with(this)
                 .load(story.photoUrl)
                 .into(binding.imgStory)
        }
    }
}