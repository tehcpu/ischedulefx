package ru.romanov.schedule.src;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import ru.romanov.schedule.AppController;
import ru.romanov.schedule.R;
import ru.romanov.schedule.adapters.ScheduleListAdapter;
import ru.romanov.schedule.utils.MySubject;
import ru.romanov.schedule.utils.StringConstants;

public class ScheduleListActivity extends Fragment {

	private ArrayList <MySubject> subjList;
	private ListView listView;
	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.list_activity_layout, container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			loadSchedule();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listView = (ListView) v.findViewById(android.R.id.list);

		ScheduleListAdapter adapter = new ScheduleListAdapter(subjList, v.getContext());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getContext(), ScheduleDayActivity.class);
				TextView dayOfWeek = (TextView) view.findViewById(R.id.schedule_lsit_item_dow);
				TextView date = (TextView) view.findViewById(R.id.schedule_list_item_date);
				i.putExtra("DayOfWeek", dayOfWeek.getText());
				i.putExtra("Date", date.getText());
				startActivity(i);
			}
		});

		// Patch timebar
		SharedPreferences sp = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		Calendar calend = Calendar.getInstance();
		// fix 01
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy  kk:mm");
		String time = sdf.format(calend.getTime());
		editor.putString(StringConstants.SHARED_LAST_SYNC_DATE, time);
		editor.commit();
	}

	/**
	 * Временная функция для тестирования. Информация берётся не с сервера, а из указанного фала
	 * @param f
	 * @return
	 */
	private static String getTestXMLStringFromLocalFile(File f) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			StringBuilder total = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				total.append(line);
			} 
			return total.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	private void loadSchedule() throws JSONException, ParseException {
		SharedPreferences sherPref = AppController.getInstance().getSharedPreferences(StringConstants.MY_SCHEDULE, Context.MODE_PRIVATE);
		Map<String,String> myMap = (Map<String, String>) sherPref.getAll();
		this.subjList = new ArrayList<MySubject>(myMap.size());
		for (String key : myMap.keySet()) {
			this.subjList.add(new MySubject(key, new JSONObject(myMap.get(key))));
		}
		
	}


}
