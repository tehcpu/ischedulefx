package ru.romanov.schedule.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import ru.romanov.schedule.AppController;
import ru.romanov.schedule.R;
import ru.romanov.schedule.adapters.ScheduleCheckListAdapter;
import ru.romanov.schedule.utils.MySubject;
import ru.romanov.schedule.utils.RequestStringsCreater;
import ru.romanov.schedule.utils.StringConstants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.check_list_activity_layout, container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences sp = AppController.getInstance().getSharedPreferences(StringConstants.SCHEDULE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		token = sp.getString(StringConstants.TOKEN, null);
		if(token==null) {
			//parampampam ERROR
			Toast.makeText(v.getContext(), "Что-то странное происходит! Где токен-то? Сейчас каааак всё сломается..", Toast.LENGTH_LONG).show();
		}
		sp = AppController.getInstance().getSharedPreferences(
				StringConstants.MY_SCHEDULE, Context.MODE_PRIVATE);
		Map<String, String> map = (Map<String, String>) sp.getAll();
		ArrayList<MySubject> scedule = new ArrayList<MySubject>(map.size());
		try {
			for (String key : map.keySet()) {
				MySubject sbj = new MySubject(key, new JSONObject(map.get(key)));
				scedule.add(sbj);
			}
			ArrayList<MySubject> sbjToCheck = new ArrayList<MySubject>();
			for (MySubject sbj : scedule) {
				if (!sbj.isChecked()) {
					sbjToCheck.add(sbj);
				}
			}
			newSubjects = sbjToCheck;
		} catch (Exception e) {
			// TODO: handle exception
		}

		listView = (ListView) v.findViewById(android.R.id.list);

		adapter =new ScheduleCheckListAdapter(newSubjects, v.getContext());
		listView.setAdapter(adapter);
		
		Button confirmButton = (Button) v.findViewById(R.id.check_confirm_button);
		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.check_confirm_button:
						StringBuilder sb = new StringBuilder();
						HashMap<String, String> idMap= adapter.getCheckedElemetsStatus();
						String reqBody = RequestStringsCreater.createConfirmCheckString(token, idMap);
						new AlertDialog.Builder(v.getContext()).setMessage(reqBody).show();
						break;
				}
			}
		});

	}
}
