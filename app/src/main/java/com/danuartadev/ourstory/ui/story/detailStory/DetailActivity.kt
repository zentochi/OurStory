package com.danuartadev.ourstory.ui.story.detailStory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.danuartadev.ourstory.data.remote.response.ListStoryItem
import com.danuartadev.ourstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listStoryItems = intent.getParcelableArrayListExtra<ListStoryItem>("list_story_item")

        if (!listStoryItems.isNullOrEmpty()) {
            val story = listStoryItems[0]
            binding.tvProfileName.text = story.name
            binding.tvUsernameDesc.text = story.description

             Glide.with(this)
                 .load(story.photoUrl)
                 .into(binding.imgStory)
        }
    }
}