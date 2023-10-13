package com.danuartadev.ourstory.ui.story.add

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.danuartadev.ourstory.R
import com.danuartadev.ourstory.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}