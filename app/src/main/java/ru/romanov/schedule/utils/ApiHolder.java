package ru.romanov.schedule.utils;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.romanov.schedule.AppController;

/**
 * Created by codebreak on 04/06/16.
 */
public class ApiHolder {
    private static ApiHolder Instance;
    private String server;
    private String TAG = "API";

    public ApiHolder() {
        Instance = this;
        server = StringConstants.MY_URI;
    }

    public void auth(final String login, final String pass, final onResponse callback) {
        StringRequest sr = new StringRequest(Request.Method.POST, server + "token.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                JSONObject object = null;

                try {
                    object = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (object != null) {
                    if (object.has("error")) {
                        callback.onFail(1);
                    } else {
                        callback.onSuccess(object);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 400) {
                        callback.onFail(1);
                    } else {
                        callback.onFail(0);
                    }
                } else {
                    callback.onFail(0);
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("grant_type", "client_credentials");
                params.put("client_id", login);
                params.put("client_secret", pass);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(sr);
    }

    public interface onResponse {
        JSONObject onSuccess(JSONObject response);
        JSONObject onFail(int code);
    }

    // Thread-safe instance # thx package
    public static ApiHolder getInstance() {
        ApiHolder localInstance = Instance;
        if (localInstance == null) {
            synchronized (ApiHolder.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ApiHolder();
                }
            }
        }
        return localInstance;
    }
}
