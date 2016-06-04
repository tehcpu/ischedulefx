package ru.romanov.schedule.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import ru.romanov.schedule.utils.*;
import ru.romanov.schedule.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts.Entity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebIconDatabase.IconListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class IScheduleActivity extends Activity {

	private SharedPreferences mSharedPreferences;
	private EditText loginEditText;
	private EditText passEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = getSharedPreferences(
				StringConstants.SCHEDULE_SHARED_PREFERENCES, MODE_PRIVATE);
		if (mSharedPreferences.getString(StringConstants.SHARED_LOGIN, null) == null
				|| mSharedPreferences.getString(StringConstants.SHARED_PASS, null) == null
				|| mSharedPreferences.getString(StringConstants.TOKEN, null) == null) {
			Editor editor = mSharedPreferences.edit();
			editor.putString(StringConstants.SHARED_LOGIN, null);
			editor.putString(StringConstants.SHARED_PASS, null);
			editor.putString(StringConstants.TOKEN, null);
			editor.commit();
			setContentView(R.layout.main);
		} else {
			// NEXT ACTIVITY
			//setContentView(R.layout.entering_layout);
			Toast.makeText(this,
					"Всё пучком! Следующая активити...", Toast.LENGTH_LONG).show();
			startMainTabActivity();
		}
		//TODO: remove it

	}

	@Override
	protected void onStart() {
		this.loginEditText = (EditText) findViewById(R.id.loginEText);
		this.passEditText = (EditText) findViewById(R.id.passEText);
		Button loginButton = (Button) findViewById(R.id.logInButton);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PostRequestAuthManager pram = new PostRequestAuthManager(
						loginEditText.getText().toString(), passEditText
								.getText().toString());

			}
		});
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void startMainTabActivity() {
		Intent intent = new Intent(this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}
	
	@SuppressWarnings("unused")
	private class PostRequestAuthManager {

		private String login;
		private String pass;
		private String token;
		private String name;
		private String email;
		private String phone;
		private ProgressDialog dialog;
		
		public PostRequestAuthManager(String login, String pass) {
			this.login = login;
			this.pass = pass;

			dialog = ProgressDialog.show(IScheduleActivity.this, "", getString(R.string.loading), true);

			ApiHolder.getInstance().auth(login, pass, new ApiHolder.onResponse() {
				public Object email;
				public String phone;
				public String name;
				public String token;

				@Override
				public JSONObject onSuccess(JSONObject response) {
					Toast.makeText(IScheduleActivity.this,
							getString(R.string.auth_success), Toast.LENGTH_LONG).show();
					try {
						this.token = response.getString("access_token");
						this.name = "qwe";
						this.phone = "qwe";
						this.email = "qwe";
					} catch (JSONException e) {
						e.printStackTrace();
					}

					saveSessionData();
					startMainTabActivity();
					dialog.dismiss();

					return null;
				}

				@Override
				public JSONObject onFail(int code) {
					switch (code) {
						case 0:
							Toast.makeText(IScheduleActivity.this, "Не получилось соединиться с серверм.", Toast.LENGTH_LONG).show();
							break;
						case 1:
							Toast.makeText(IScheduleActivity.this,
									getString(R.string.login_error), Toast.LENGTH_LONG).show();
							break;
						default:break;
					}
					dialog.dismiss();
					return null;
				}
			});
		}

		
		private void saveSessionData() {
			Editor editor = IScheduleActivity.this.mSharedPreferences.edit();
			editor.putString(StringConstants.SHARED_LOGIN, this.login);
			editor.putString(StringConstants.SHARED_PASS, this.pass);
			editor.putString(StringConstants.TOKEN, this.token);
			editor.putString(StringConstants.SHARED_NAME, this.name);
			editor.putString(StringConstants.SHARED_PHONE, this.phone);
			editor.putString(StringConstants.SHARED_EMAIL, this.email);
			editor.commit();
		}
		

	}
}
