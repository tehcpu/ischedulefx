package ru.trohimchuk.schedule.src;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ru.trohimchuk.schedule.AppController;
import ru.trohimchuk.schedule.R;
import ru.trohimchuk.schedule.adapters.CurrentDayAdapter;
import ru.trohimchuk.schedule.models.Subject;
import ru.trohimchuk.schedule.utils.ApiHolder;
import ru.trohimchuk.schedule.utils.StringConstants;

public class ScheduleDayActivity extends AppCompatActivity {

    private static final String TAG = "DayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_day);

        final ListView listView = (ListView) findViewById(R.id.subjects_list);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String dow = intent.getStringExtra("DayOfWeek");
        String date = intent.getStringExtra("Date");

        final String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
        dow = String.valueOf(Arrays.asList(days).indexOf(dow));

        Log.d(TAG, "dow --> "+dow+" | date --> "+date);

        ApiHolder.getInstance().loadCurrentDay(date, dow, new ApiHolder.onResponse() {
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

                    // fix order
                    sortSchedule(subjects);

                    if (listView != null) {
                        listView.setAdapter(new CurrentDayAdapter(AppController.getInstance().getApplicationContext(), subjects));
                    }
                }
                return null;
            }

            @Override
            public JSONObject onFail(int code) {
                progressBar.setVisibility(View.GONE);
                if (code == 0) {
                    Toast.makeText(getApplicationContext(), "Не удалось получить данные от сервера, проверьте соединение.", Toast.LENGTH_LONG).show();
                } else if (code == 1) {
                    Toast.makeText(getApplicationContext(), "На этот день нет предметов.", Toast.LENGTH_LONG).show();
                } else if (code == 2) {
                    Toast.makeText(getApplicationContext(),
                            "Истекло время действия токена.. Пожалуйста, перелогиньтесь", Toast.LENGTH_LONG).show();
                    SharedPreferences sp = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(StringConstants.SHARED_LOGIN, null);
                    editor.putString(StringConstants.SHARED_PASS, null);
                    editor.putString(StringConstants.TOKEN, null);
                    editor.commit();
                    Intent myIntent = new Intent(ScheduleDayActivity.this, IScheduleActivity.class);
                    ScheduleDayActivity.this.startActivity(myIntent);
                    finish();
                }
                return null;
            }
        });
    }

    // for kitkat and up

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
}
