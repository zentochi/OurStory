package com.danuartadev.ourstory.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.danuartadev.ourstory.databinding.ActivitySignupBinding
import com.danuartadev.ourstory.ui.ViewModelFactory
import com.danuartadev.ourstory.utils.Result

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
//        enableSignupButton()
    }

    private fun registerAccount() {
        val nameText = binding.nameEditText.text.toString()
        val emailEditText = binding.emailEditText.text.toString()
        val passwordText = binding.passwordEditText.text.toString()
        viewModel.register(nameText, emailEditText, passwordText).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        showToast(result.data.message.toString())
                    }
                    is Result.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoadingSignup: Boolean) {
        if (isLoadingSignup) {
            binding.signupProgressBar.visibility = View.VISIBLE
        } else {
            binding.signupProgressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    private fun createTextWatcher(): TextWatcher {
//        return object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                enableSignupButton()
//            }
//        }
//    }


//    private fun enableSignupButton() {
//        val nameText = binding.nameEditText.text.toString()
//        val emailEditText = binding.emailEditText
//        val passwordText = binding.passwordEditText.text.toString()
//
//        val isEmailValid = emailEditText.error == null
//        val isSubmitButtonEnabled = nameText.isNotEmpty() && isEmailValid && passwordText.length >= 8
//
//        binding.signupButton.isEnabled = isSubmitButtonEnabled
//    }

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

//        binding.apply {
//            nameEditText.addTextChangedListener(createTextWatcher())
//            emailEditText.addTextChangedListener(createTextWatcher())
//            passwordEditText.addTextChangedListener(createTextWatcher())
//        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val nameText = binding.nameEditText.text.toString()
            val emailText = binding.emailEditText.text.toString()
            val passwordText = binding.passwordEditText.text.toString()
            registerAccount()
//            viewModel.register(nameText, emailText, passwordText)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}