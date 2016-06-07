package ru.romanov.schedule.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.romanov.schedule.AppController;
import ru.romanov.schedule.R;
import ru.romanov.schedule.adapters.CurrentDayAdapter;
import ru.romanov.schedule.adapters.ScheduleCheckListAdapter;
import ru.romanov.schedule.models.Subject;
import ru.romanov.schedule.utils.ApiHolder;
import ru.romanov.schedule.utils.MySubject;
import ru.romanov.schedule.utils.RequestStringsCreater;
import ru.romanov.schedule.utils.StringConstants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class UpdateListActivity extends Fragment {

	private List<MySubject> newSubjects;
	private ScheduleCheckListAdapter adapter;
	private String token;
	private View v;
	private ListView listView;
	private String TAG = "UpdatesActivity";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.check_list_activity_layout, container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

		final ListView listView = (ListView) v.findViewById(R.id.list_updates_item);

		final String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

		ApiHolder.getInstance().loadUpdates(new ApiHolder.onResponse() {
			@Override
			public JSONObject onSuccess(Object response) {
				Log.d(TAG, "resp --> "+response);
				JSONArray array = null;
				array = (JSONArray) response;

				ArrayList<Subject> subjects = new ArrayList<>();

				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						try {
							JSONObject subjObject = array.getJSONObject(i);
							Subject subject = new Subject();

							subject.setName(subjObject.getString("name"));
							subject.setStart_date(subjObject.getString("start_date"));
							subject.setEnd_date(subjObject.getString("end_date"));
							subject.setDayOfWeek(days[subjObject.getInt("week_day")]);
							subject.setTime(subjObject.getString("time"));
							subject.setSquad(subjObject.getString("squad"));
							subject.setClassroom(subjObject.getString("classroom"));

							subjects.add(subject);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					if (listView != null) {
						listView.setAdapter(new CurrentDayAdapter(AppController.getInstance().getApplicationContext(), subjects));
					}
				}
				return null;
			}

			@Override
			public JSONObject onFail(int code) {
				return null;
			}
		});

	}
}
