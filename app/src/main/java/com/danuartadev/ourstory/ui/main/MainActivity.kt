package com.danuartadev.ourstory.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.danuartadev.ourstory.databinding.ActivityMainBinding
import com.danuartadev.ourstory.ui.ViewModelFactory
import com.danuartadev.ourstory.ui.story.add.AddStoryActivity
import com.danuartadev.ourstory.ui.welcome.WelcomeActivity
import com.danuartadev.ourstory.utils.Result

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

                // Observe the loading status
//                viewModel.isLoadingStories.observe(this) { isLoading ->
//                    binding.mainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//                }
            }
            // logging status
//            binding.tvError.visibility = View.VISIBLE
//            binding.tvError.text = "current status:\n" +
//                    "email:${user.email}\ntoken:${user.token}\nisLogin:${user.isLogin}"
            setupRecyclerView()
            viewStories()
            setupView()
            setupAction()

            binding.refreshRvMain.setOnRefreshListener {
                viewStories()
                binding.refreshRvMain.isRefreshing = false
            }
        }

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

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
        binding.fabUpload.setOnClickListener {
            val moveIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(moveIntent)
        }
    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager

        // Initialize the adapter and set it to the RecyclerView
        adapter = MainAdapter()
        binding.rvMain.adapter = adapter

//        // Observe the stories data
//        viewModel.getStoriesResponse.observe(this) { storyResponse ->
//            if (!storyResponse.error!!) {
//                adapter.submitList(storyResponse.listStory)
//            } else {
//                // Handle the error condition here
//                Log.d(TAG, "Error fetching stories: ${storyResponse.message}")
//            }
//        }

        // Handle the error message visibility
//        viewModel.errorMessage.observe(this) { errorMessage ->
//            if (errorMessage != null) {
//                binding.tvErrorMain.visibility = View.VISIBLE
//                binding.tvErrorMain.text = errorMessage
//            } else {
//                binding.tvErrorMain.visibility = View.GONE
//            }
//        }

    }

    private fun viewStories() {
        viewModel.getStories().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        adapter.submitList(result.data.listStory)
                    }
                    is Result.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
            binding.mainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}