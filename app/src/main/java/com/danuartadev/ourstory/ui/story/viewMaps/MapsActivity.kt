package com.danuartadev.ourstory.ui.story.viewMaps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.danuartadev.ourstory.R
import com.danuartadev.ourstory.databinding.ActivityMapsBinding
import com.danuartadev.ourstory.ui.ViewModelFactory
import com.danuartadev.ourstory.utils.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModels<MapsViewModel>() {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        addMarker()
        setMapStyle()
        getMyLastLocation()
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun addMarker() {
        viewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    result.data.listStory.forEach { data ->
                        val latLng = LatLng(data.lat!!, data.lon!!)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(data.name)
                                .snippet(data.description)
                        )
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    showToast("Error: ${result.error}")
                }

                else -> {}
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
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

    private fun checkPermission(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if ( checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    mMap.isMyLocationEnabled = true
                    showCurrentLocation(location)
                } else {
                    showToast(getString(R.string.location_not_found))
                }
            }
        } else {
            requestPermissionLauncher.launch( arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION) )
        }
    }

    private fun showCurrentLocation(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.mapProgressbar.visibility = View.VISIBLE else binding.mapProgressbar.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MapsActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}