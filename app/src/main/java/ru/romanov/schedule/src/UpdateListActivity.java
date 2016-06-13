package ru.romanov.schedule.src;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import ru.romanov.schedule.AppController;
import ru.romanov.schedule.R;
import ru.romanov.schedule.adapters.UpdateAdapter;
import ru.romanov.schedule.models.Subject;
import ru.romanov.schedule.utils.ApiHolder;
import ru.romanov.schedule.utils.MySubject;
import ru.romanov.schedule.utils.StringConstants;

public class UpdateListActivity extends Fragment {

	private List<MySubject> newSubjects;
	private UpdateAdapter adapter;
	private String token;
	private View v;
	private ListView listView;
	private String TAG = "UpdatesActivity";
	private String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.check_list_activity_layout, container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

		final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		listView = (ListView) v.findViewById(R.id.list_updates_item);
		Button push = (Button) v.findViewById(R.id.check_confirm_button);

		push.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFeedback();
			}
		});


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

							subject.setId(subjObject.getInt("id"));
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

					// fix order (bubble)
					sortSchedule(subjects);
					sortScheduleDay(subjects);

					if (listView != null) {
						adapter = new UpdateAdapter(AppController.getInstance().getApplicationContext(), subjects);
						listView.setAdapter(adapter);
					}

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
				return null;
			}

			@Override
			public JSONObject onFail(int code) {
				progressBar.setVisibility(View.GONE);
				if (code == 0) {
					Toast.makeText(getContext().getApplicationContext(), "Не удалось получить данные от сервера, проверьте соединение.", Toast.LENGTH_LONG).show();
				} else if (code == 1) {
					Toast.makeText(getContext(), "Обновлений для вас не найдено.", Toast.LENGTH_LONG).show();
					adapter = new UpdateAdapter(getContext(), new ArrayList());
					listView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
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

	public void sendFeedback() {
		JSONArray data = new JSONArray();
		if (adapter != null) {
			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.getItem(i).getChecked() != 0) {
					//Log.d(TAG, "check it out --> " + getResources().getResourceEntryName(v.findViewById(adapter.getItem(i).getChecked()).getId()) + " | " + adapter.getItem(i).getId());
					JSONObject obj = new JSONObject();
					try {
						obj.put("id", adapter.getItem(i).getId());
						obj.put("choice", getResources().getResourceEntryName(v.findViewById(adapter.getItem(i).getChecked()).getId()));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					data.put(obj.toString());
				}
			}
		}


		Log.d(TAG, "json --> "+data+"--> lvc "+listView.getCount());

		if (data.length() > 0) {

			ApiHolder.getInstance().sendFeedback(data, new ApiHolder.onResponse() {
				@Override
				public JSONObject onSuccess(Object response) {
					Toast.makeText(getContext().getApplicationContext(), "Данные успешно отправлены.", Toast.LENGTH_LONG).show();
					adapter = new UpdateAdapter(getContext(), new ArrayList());
					onStart();
					return null;
				}

				@Override
				public JSONObject onFail(int code) {
					if (code == 0) {
						Toast.makeText(getContext().getApplicationContext(), "Не удалось получить данные от сервера, проверьте соединение.", Toast.LENGTH_LONG).show();
					} else if (code == 1) {
						//Toast.makeText(getContext(), "Обновлений для вас не найдено.", Toast.LENGTH_LONG).show();
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

	void sortSchedule(ArrayList<Subject> arr) {
		for (int i = arr.size() - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (arr.get(j).getTime() != null) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						if (Objects.equals(arr.get(j).getTime().substring(0, 2), "9:")) arr.get(j).setTime("0"+arr.get(j).getTime());
						if (Objects.equals(arr.get(j + 1).getTime().substring(0, 2), "9:")) arr.get(j+1).setTime("0"+arr.get(j+1).getTime());
						if (Integer.parseInt(arr.get(j).getTime().substring(0, 2)) > Integer.parseInt(arr.get(j + 1).getTime().substring(0, 2))) {
							Subject t = arr.get(j);
							arr.set(j, arr.get(j + 1));
							arr.set(j + 1, t);
						}
					}
				}
			}
		}
	}

	void sortScheduleDay(ArrayList<Subject> arr) {
		for (int i = arr.size() - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (arr.get(j).getDayOfWeek() != null) {
					if (Arrays.asList(days).indexOf(arr.get(j).getDayOfWeek()) > Arrays.asList(days).indexOf(arr.get(j+1).getDayOfWeek())) {
						Subject t = arr.get(j);
						arr.set(j, arr.get(j + 1));
						arr.set(j + 1, t);
					}
				}
			}
		}
	}
}
