package ru.romanov.schedule.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


import ru.romanov.schedule.AppController;
import ru.romanov.schedule.R;
import ru.romanov.schedule.utils.ApiHolder;
import ru.romanov.schedule.utils.RequestStringsCreater;
import ru.romanov.schedule.utils.StringConstants;
import ru.romanov.schedule.utils.XMLParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainTabActivity extends AppCompatActivity {

	private static final String TAG = "MainTabActivity";
	TextView lastSyncTV;
	private FragmentTabHost mTabHost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);
		lastSyncTV = (TextView) findViewById(R.id.maintab_last_sync);

		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.schedule)).setIndicator(getString(R.string.schedule)),
				ScheduleListActivity.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.updates)).setIndicator(getString(R.string.updates)),
				UpdateListActivity.class, null);

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		String lastSync = sp.getString(StringConstants.SHARED_LAST_SYNC_DATE, null);
		Log.d(TAG, lastSync+"<-- right here");
		if(lastSync==null)
			lastSyncTV.setText("-");
		else
			lastSyncTV.setText(lastSync);
		ApiHolder.getInstance().validateToken(new ApiHolder.onResponse() {
			@Override
			public JSONObject onSuccess(Object response) {
				return null;
			}

			@Override
			public JSONObject onFail(int code) {
				Toast.makeText(getApplicationContext(),
						"Истекло время действия токена.. Пожалуйста, перелогиньтесь", Toast.LENGTH_LONG).show();
				SharedPreferences sp = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(StringConstants.SHARED_LOGIN, null);
				editor.putString(StringConstants.SHARED_PASS, null);
				editor.putString(StringConstants.TOKEN, null);
				editor.commit();
				Intent myIntent = new Intent(MainTabActivity.this, IScheduleActivity.class);
				MainTabActivity.this.startActivity(myIntent);
				finish();
				return null;
			}
		});

		ApiHolder.getInstance().setUserInfo();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit:
			AlertDialog alert = getExitAlertDialog();
			alert.show();
			break;
		case R.id.menu_info:
			Intent intent2 = new Intent(this, UserInfoDialogActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}

		return true;
	}

	private AlertDialog getExitAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.sure_to_exit))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.dialog_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SharedPreferences pref = getSharedPreferences(
										StringConstants.SCHEDULE_SHARED_PREFERENCES,
										MODE_PRIVATE);
								SharedPreferences.Editor editor = pref.edit();
								for (String key : pref.getAll().keySet()) {
									editor.remove(key);
								}
								editor.commit();
								SharedPreferences schedule = getSharedPreferences(
										StringConstants.MY_SCHEDULE,
										MODE_PRIVATE);
								editor = schedule.edit();
								for (String key : pref.getAll().keySet()) {
									editor.remove(key);
								}
								editor.commit();
								MainTabActivity.this.finish();
							}
						})
				.setNegativeButton(getString(R.string.dialog_no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		return builder.create();
	}

}
