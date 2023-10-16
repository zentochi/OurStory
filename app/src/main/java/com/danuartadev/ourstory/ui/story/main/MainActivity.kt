package com.danuartadev.ourstory.ui.story.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.danuartadev.ourstory.R
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
                //is logged in
                setupView()
                getStories()
                setupRecyclerView()
                setupAction()
            }
            // logging status
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "current status:\n" +
                    "email:${user.email}\n" +
                    "token:${user.token}\n" +
                    "isLogin:${user.isLogin}"
            showToast(user.email)
        }
        binding.refreshRvMain.setOnRefreshListener {
            Log.d(TAG, "onRefresh: ${binding.refreshRvMain.isRefreshing}")
            getStories()
            binding.refreshRvMain.isRefreshing = false
        }
    }
//    override fun onResume() {
//        super.onResume()
//        viewStories()
//    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                val intent = Intent(this, WelcomeActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            R.id.action_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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
    }

    private fun setupAction() {
        binding.fabUpload.setOnClickListener {
            val moveIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager

        adapter = MainAdapter()
        binding.rvMain.adapter = adapter
    }

    private fun getStories() {
        viewModel.getStories().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        adapter.submitList(result.data.listStory)
                        Log.d(TAG, "result: ${result.data.listStory}")
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        Log.d(TAG, "result: ${result.error}")
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