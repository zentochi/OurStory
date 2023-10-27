package com.danuartadev.ourstory.ui.story.addStory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.danuartadev.ourstory.R
import com.danuartadev.ourstory.databinding.ActivityAddStoryBinding
import com.danuartadev.ourstory.ui.ViewModelFactory
import com.danuartadev.ourstory.ui.story.homeStory.MainActivity
import com.danuartadev.ourstory.utils.Result
import com.danuartadev.ourstory.utils.getImageUri
import com.danuartadev.ourstory.utils.reduceFileImage
import com.danuartadev.ourstory.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentLat: Double? = null
    private var currentLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("StringFormatInvalid")
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
        val name = intent.getStringExtra(NAME)
        binding.tvUsers.text = getString(R.string.username_upload, name)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Image Picker", "No image media selected")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions())
    { permission ->
        when {
            permission[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permission[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {
                showToast(getString(R.string.permission_location_revoked))
            }
        }
    }

    private fun setupAction() {
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnCamera.setOnClickListener {
            startCamera()
        }
        binding.btnPostStory.setOnClickListener {
            val lat = currentLat
            val lng = currentLng
            if (binding.switchShareLoc.isChecked) {
                if (lat != null && lng != null) {
                    Log.d(TAG, "setupAction: $lat, $lng")
                    uploadStory(lat, lng)
                } else {
                    showToast("Location data is not available.")
                }
            } else if (!binding.switchShareLoc.isChecked){
                uploadStory(null, null)
            }
        }
        binding.switchShareLoc.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.tvShareLoc.text = getString(R.string.share_location_on)
                getMyLastLocation()
            } else {
                binding.tvShareLoc.text = getString(R.string.share_location_off)
            }
        }
    }


    private fun getMyLastLocation() {
        if ( checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLng = location.longitude
                    Log.d(TAG, "getMyLastLocation: savedlat -> $currentLat, $currentLng")
                } else {
                    showToast(getString(R.string.location_not_found))
                }
            }
        } else {
            requestPermissionLauncher.launch( arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION) )
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun uploadStory(lat: Double?, lng: Double?) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.tvDesc.text.toString()
            showLoading(true)

            viewModel.uploadImage(imageFile, description, lat, lng).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showToast(result.data.message)
                            Log.d(TAG, result.data.message)
                            showLoading(false)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        is Result.Error -> {
                            showToast(result.error)
                            Log.d(TAG, result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image uri", "showImage: $it")
            binding.previewImage.setImageURI(it)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val NAME = "name"
        private const val TAG = "AddStoryActivity"
    }

}