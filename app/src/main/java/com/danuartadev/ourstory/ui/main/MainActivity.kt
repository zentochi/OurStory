package com.danuartadev.ourstory.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.danuartadev.ourstory.data.remote.response.ListStoryItem
import com.danuartadev.ourstory.databinding.ActivityMainBinding
import com.danuartadev.ourstory.ui.ViewModelFactory
import com.danuartadev.ourstory.ui.story.add.AddStoryActivity
import com.danuartadev.ourstory.ui.story.add.CameraActivity
import com.danuartadev.ourstory.ui.story.detail.DetailActivity
import com.danuartadev.ourstory.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupRecyclerView()

                // Observe the loading status
                viewModel.isLoadingStories.observe(this) { isLoading ->
                    binding.mainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
            // logging status
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "current status:\n" +
                    "email:${user.email}\ntoken:${user.token}\nisLogin:${user.isLogin}"

            binding.refreshRvMain.setOnRefreshListener {
                viewModel.getStories()
                binding.rvMain.scrollToPosition(0)
                binding.refreshRvMain.isRefreshing = false
            }
        }

        viewModel.getStories()
        setupView()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
        binding.rvMain.scrollToPosition(0)
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager

        // Initialize the adapter and set it to the RecyclerView
        adapter = MainAdapter()
        binding.rvMain.adapter = adapter

        // Observe the stories data
        viewModel.getStoriesResponse.observe(this) { storyResponse ->
            if (!storyResponse.error!!) {
                adapter.submitList(storyResponse.listStory)
            } else {
                // Handle the error condition here
                Log.d(TAG, "Error fetching stories: ${storyResponse.message}")
            }
        }

        // Handle the error message visibility
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.tvErrorMain.visibility = View.VISIBLE
                binding.tvErrorMain.text = errorMessage
            } else {
                binding.tvErrorMain.visibility = View.GONE
            }
        }

    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
        binding.fabUpload.setOnClickListener {
            val moveIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private fun showLoading(isLoadingMain: Boolean) {
        if (isLoadingMain) {
            binding.mainProgressBar.visibility = View.VISIBLE
        } else {
            binding.mainProgressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}