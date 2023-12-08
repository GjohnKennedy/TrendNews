package com.example.phase1.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.phase1.databinding.FragmentWeatherBinding
import com.example.phase1.weather.viewmoel.WeatherViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private val REQUEST_CODE_PERMISSIONS = 100
    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels()

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getWeatherData(location.latitude, location.longitude)
            locationManager.removeUpdates(this)
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    private lateinit var locationManager: LocationManager

    private val locationSettingsContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                requestLocationUpdates()
            } else {
                showLocationSettingsDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPermissions()


        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->

            Log.e("myData", "${weatherResponse.city}")

            binding.apply {

                val currentWeather = weatherResponse.weatherList.firstOrNull()

                textViewCity.text = "${weatherResponse.city.name}"

                currentWeather?.let {
                    textViewTemperature.text =
                        "Temperature: ${it.main.temp}°C  Humidity :  ${it.main.humidity} %"

                    textViewAirQuality.text = "Air Speed: ${it.wind.speed}"

                    val weatherDescription = it.weather.firstOrNull()

                    weatherDescription?.let {
                        textViewDescriptionMain.text = it.main.toString()
                        textViewDescription.text = it.description.toString()
                    }
                }

                refreshButton.setOnClickListener {

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("myData", "${errorMessage}")
        }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        Log.e("myData", "${latitude} +  ${longitude}")

        viewModel.getWeatherData(latitude, longitude)
    }

    private fun getPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) else arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (isAnyOfPermissionsNotGranted(requiredPermissions)) {
            requestPermissions(
                requiredPermissions,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            checkLocationSettings()
        }
    }

    private fun isAnyOfPermissionsNotGranted(requiredPermissions: Array<String>): Boolean {
        for (permission in requiredPermissions) {
            val checkSelfPermissionResult =
                ContextCompat.checkSelfPermission(requireContext(), permission)
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
                return true
            }
        }
        return false
    }

    private fun checkLocationSettings() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationSettingsDialog()
        } else {
            requestLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (REQUEST_CODE_PERMISSIONS == requestCode) {
                Toast.makeText(requireContext(), "Permissions granted!", Toast.LENGTH_SHORT)
                    .show();
                checkLocationSettings()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Location permissions are mandatory to use BLE features on Android 6.0 or higher",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLocationSettingsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Location is turned off")
            .setMessage("Please turn on location to use this feature.")
            .setPositiveButton("Settings") { _, _ ->
//                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivityForResult(callGPSSettingIntent)
                locationSettingsContract.launch(callGPSSettingIntent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
