package ru.romanov.schedule.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
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

    public void setUserInfo() {
        final SharedPreferences mSharedPreferences = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        StringRequest sr = new StringRequest(Request.Method.POST, StringConstants.MY_URI + "resource.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("user_name")) {
                    try {
                        JSONObject resp = new JSONObject(response);

                        String name = resp.getString("user_name");
                        String phone = resp.getString("user_phone");
                        String email = resp.getString("user_email");

                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(StringConstants.SHARED_NAME, name);
                        editor.putString(StringConstants.SHARED_PHONE, phone);
                        editor.putString(StringConstants.SHARED_EMAIL, email);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("access_token", mSharedPreferences.getString(StringConstants.TOKEN, null));
                params.put("method", "getUserInfo");
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

    public void validateToken(final ApiHolder.onResponse callback) {
        final SharedPreferences mSharedPreferences = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        StringRequest sr = new StringRequest(Request.Method.POST, StringConstants.MY_URI + "resource.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(StringConstants.SHARED_LOGIN, null);
                        editor.putString(StringConstants.SHARED_PASS, null);
                        editor.putString(StringConstants.TOKEN, null);
                        editor.commit();
                        callback.onFail(1);
                    }
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("access_token", mSharedPreferences.getString(StringConstants.TOKEN, null));
                params.put("method", "getUserInfo");
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

    public void loadCurrentDay(final String date, final String dow, final onResponse callback) {
        final SharedPreferences mSharedPreferences = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        StringRequest sr = new StringRequest(Request.Method.POST, StringConstants.MY_URI + "resource.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "curr_day --> APi "+response);
                if (response.length()>1) {
                    try {
                        callback.onSuccess(new JSONArray(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onFail(1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(0);

                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(StringConstants.SHARED_LOGIN, null);
                        editor.putString(StringConstants.SHARED_PASS, null);
                        editor.putString(StringConstants.TOKEN, null);
                        editor.commit();
                        callback.onFail(1);
                    }
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("access_token", mSharedPreferences.getString(StringConstants.TOKEN, null));
                params.put("method", "loadCurrentDay");
                params.put("date", date);
                params.put("weekday", dow);
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

    public void loadUpdates(final onResponse callback) {
        final SharedPreferences mSharedPreferences = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        StringRequest sr = new StringRequest(Request.Method.POST, StringConstants.MY_URI + "resource.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "curr_day --> APi "+response);
                if (response.length()>1) {
                    try {
                        callback.onSuccess(new JSONArray(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onFail(1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(0);

                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(StringConstants.SHARED_LOGIN, null);
                        editor.putString(StringConstants.SHARED_PASS, null);
                        editor.putString(StringConstants.TOKEN, null);
                        editor.commit();
                        callback.onFail(1);
                    }
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("access_token", mSharedPreferences.getString(StringConstants.TOKEN, null));
                params.put("method", "loadUpdates");
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

    public void sendFeedback(final JSONObject data, final onResponse callback) {
        final SharedPreferences mSharedPreferences = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        StringRequest sr = new StringRequest(Request.Method.POST, StringConstants.MY_URI + "resource.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>1) {
                    try {
                        callback.onSuccess(new JSONArray(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onFail(1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(0);

                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(StringConstants.SHARED_LOGIN, null);
                        editor.putString(StringConstants.SHARED_PASS, null);
                        editor.putString(StringConstants.TOKEN, null);
                        editor.commit();
                        callback.onFail(1);
                    }
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("access_token", mSharedPreferences.getString(StringConstants.TOKEN, null));
                params.put("method", "sendFeedback");
                params.put("data", data.toString());
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

    // Common callback interface

    public interface onResponse {
        JSONObject onSuccess(Object response);
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
