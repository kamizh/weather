package com.example.weather
import android.Manifest
import android.app.AlertDialog
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import  RequestWeather
class MainActivity : AppCompatActivity() {

    private lateinit var mainText: TextView;
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textCity: TextView;
    private var cityName = "";
    private var doc: Document? = null;
    private lateinit var requestWeather: RequestWeather;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textCity = findViewById(R.id.textCity);
        requestWeather = RequestWeather();
        mainText = findViewById(R.id.textGradusMain);
        // установка только портретной ориентации
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        // установка полного экрана на всех устройствах
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //принудительная проверка на подключение к интернету
        while (!isInternetAvailable(this)) {
            Toast.makeText(this, "Подключитесь к интернету!", Toast.LENGTH_SHORT).show();
        }

        // принудительная проверка на доступ к геолокации
        checkLocationPermission(this);
        //создание клиента служб определения последнего местоположения
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //получение последнего местоположения и с последующим получением города
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: android.location.Location? ->
                if (location == null) {
                    Log.d("opredGeo", "Местоположение недоступно");
                } else {
                    cityName = getCity(this, location.latitude, location.longitude);
                    textCity.setText(cityName);

                    }
                }
            }



    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    }

    fun checkLocationPermission(activity: Activity) {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(
                activity,
                locationPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение на геопозицию уже предоставлено
            // Продолжаем выполнение кода
        } else {
            // Запрашиваем разрешение на геопозицию
            ActivityCompat.requestPermissions(activity, arrayOf(locationPermission), 101)
        }
    }

    fun showPermissionDeniedDialog(activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle("Предупреждение")
        dialogBuilder.setMessage("Для работы приложения необходимо разрешение на доступ к геопозиции. Перейти в настройки и предоставить разрешение?")
        dialogBuilder.setPositiveButton("Да") { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        }
        dialogBuilder.setNegativeButton("Отмена") { dialog, which ->
            // Пользователь отменил запрос разрешения
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Пользователь разрешил доступ к геопозиции
            // Продолжаем выполнение кода
        } else {
            // Пользователь отказал в доступе к геопозиции
            showPermissionDeniedDialog(this)
        }
    }

    fun getCity(activity: Activity, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val cityName = addresses[0]?.locality
            return cityName.toString()
        } else {
            Log.d("ErrorCity", "Проблема с городом")
        }
        return ""
    }
}
