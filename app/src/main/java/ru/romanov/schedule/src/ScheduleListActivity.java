package ru.romanov.schedule.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.romanov.schedule.AppController;
import ru.romanov.schedule.R;
import ru.romanov.schedule.adapters.ScheduleListAdapter;
import ru.romanov.schedule.utils.MySubject;
import ru.romanov.schedule.utils.MySubjectUpdateManager;
import ru.romanov.schedule.utils.RequestStringsCreater;
import ru.romanov.schedule.utils.StringConstants;
import ru.romanov.schedule.utils.XMLParser;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
		/*TextView tv = (TextView) findViewById(android.R.id.empty);
		File f = new File(Environment.getDataDirectory().getPath().concat("/schdule_response.xml"));
		String xmlSchedule = getTestXMLStringFromLocalFile(f);
		tv.setText(xmlSchedule);
		try{
			ArrayList<MySubject> sbjects = new ArrayList<MySubject>();
			for (String key : myMap.keySet()) {
				sbjects.add(new MySubject(key, new JSONObject(myMap.get(key))));
			}
			MySubjectUpdateManager manager = XMLParser.parseXMLScheduleResponse(xmlSchedule);
			HashMap<String, String> map = new HashMap<String, String>();
			ArrayList<MySubject> arr = new ArrayList<MySubject>();
			for (MySubject sbj : manager.getSubjectsToAdd()) {
				map.put(sbj.getId(), sbj.toJSONObject().toString());
				arr.add(new MySubject(sbj.getId(),new JSONObject(map.get(sbj.getId()))));
			}
			SharedPreferences.Editor editor = sherPref.edit();
			for (String key : map.keySet()) {
				editor.putString(key, map.get(key));
			}
			editor.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		listView = (ListView) v.findViewById(android.R.id.list);

		ScheduleListAdapter adapter = new ScheduleListAdapter(subjList, v.getContext());
		listView.setAdapter(adapter);
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
