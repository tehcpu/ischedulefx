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
import ru.romanov.schedule.adapters.UpdateAdapter;
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
import android.widget.ProgressBar;
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

		final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		final ListView listView = (ListView) v.findViewById(R.id.list_updates_item);

		final String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

		ApiHolder.getInstance().loadUpdates(new ApiHolder.onResponse() {
			@Override
			public JSONObject onSuccess(Object response) {
				progressBar.setVisibility(View.GONE);
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
						listView.setAdapter(new UpdateAdapter(AppController.getInstance().getApplicationContext(), subjects));
					}
				}
				return null;
			}

			@Override
			public JSONObject onFail(int code) {
				progressBar.setVisibility(View.GONE);
				if (code == 0) {
					Toast.makeText(getContext(), "Не удалось получить данные от сервера, проверьте соединение.", Toast.LENGTH_LONG).show();
				} else if (code == 1) {
					Toast.makeText(getContext(), "Обновлений для вас не найдено.", Toast.LENGTH_LONG).show();
				} else if (code == 2) {
					Toast.makeText(getContext(),
							"Истекло время действия токена.. Пожалуйста, перелогиньтесь", Toast.LENGTH_LONG).show();
					SharedPreferences sp = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString(StringConstants.SHARED_LOGIN, null);
					editor.putString(StringConstants.SHARED_PASS, null);
					editor.putString(StringConstants.TOKEN, null);
					editor.commit();
					Intent myIntent = new Intent(getContext(), IScheduleActivity.class);
					UpdateListActivity.this.startActivity(myIntent);
					getActivity().finish();
				}
				return null;
			}
		});

	}
}
