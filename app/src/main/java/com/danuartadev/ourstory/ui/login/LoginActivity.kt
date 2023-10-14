package com.danuartadev.ourstory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.danuartadev.ourstory.data.pref.UserModel
import com.danuartadev.ourstory.databinding.ActivityLoginBinding
import com.danuartadev.ourstory.ui.ViewModelFactory
import com.danuartadev.ourstory.ui.main.MainActivity
import com.danuartadev.ourstory.utils.Result

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        enableLoginButton()
    }

    private fun loginAccount() {
        // to be improved
        val emailText = binding.emailEditText.text.toString()
        val passwordText = binding.passwordEditText.text.toString()
        viewModel.login(emailText, passwordText).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        val email = binding.emailEditText.text.toString()
                        val token = result.data.loginResult?.token ?: ""
                        viewModel.saveSession(UserModel(email, token))
                        result.data.message?.let { showToast(it) }
                        showLoading(false)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    is Result.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
//            if (isSuccess) {
//                Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT).show()
//                showLoading(false)
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                viewModel.errorMessage.observe(this) { error ->
//                    if (!error.isNullOrBlank()) {
//                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                showLoading(false)
//            }
        }
//        viewModel.isLoadingLogin.observe(this) {
//            showLoading(it)
//        }

    private fun showLoading(isLoading: Boolean) {
        binding.loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enableLoginButton()
            }
        }
    }

    private fun enableLoginButton() {

        val emailText = binding.emailEditText.text.toString()
        val passwordText = binding.passwordEditText.text.toString()
        val isEmailValid = binding.emailEditText.error == null
        val isSubmitButtonEnabled = isEmailValid && passwordText.length >= 8
        binding.loginButton.isEnabled = isSubmitButtonEnabled
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) 
        }
        supportActionBar?.hide()
        
        binding.apply {
            emailEditText.addTextChangedListener(createTextWatcher())
            passwordEditText.addTextChangedListener(createTextWatcher())
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val emailText = binding.emailEditText.text.toString()
            loginAccount()
//            viewModel.login(emailText, passwordText)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}
