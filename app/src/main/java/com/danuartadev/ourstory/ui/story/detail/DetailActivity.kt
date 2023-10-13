package com.danuartadev.ourstory.ui.story.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.danuartadev.ourstory.R
import com.danuartadev.ourstory.data.remote.response.ListStoryItem
import com.danuartadev.ourstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve Parcelable data from the intent
        val listStoryItems = intent.getParcelableArrayListExtra<ListStoryItem>("list_story_item")

        if (listStoryItems != null && listStoryItems.isNotEmpty()) {
            // Bind data to your layout
            val story = listStoryItems[0] // Since we passed a single item in the list
            binding.tvProfileName.text = story.name
            binding.tvUsernameDesc.text = story.description

            // Load the image using Glide or another image loading library
             Glide.with(this)
                 .load(story.photoUrl)
                 .into(binding.imgStory)
        }
    }
}