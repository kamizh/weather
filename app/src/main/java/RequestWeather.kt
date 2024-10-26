import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RequestWeather {
    fun requestJson(context: Context, latitude: Double, longitude: Double) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=b5bdc154294a49fbae762943242510&q=$latitude,$longitude&days=5&aqi=yes&alerts=no"
        var requestDeque = Volley.newRequestQueue(context);
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.d("Good","GoodLuck")
            },
            Response.ErrorListener { error ->
                Log.d("Error","Error");
            }
        )
        requestDeque.add(jsonObjectRequest);

    }
    fun parseWeatherData(result : String)
    {
        val jsonObject = JSONObject(result);

    }
}
