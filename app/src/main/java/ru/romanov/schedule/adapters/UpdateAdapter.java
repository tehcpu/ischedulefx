package ru.romanov.schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.romanov.schedule.R;
import ru.romanov.schedule.models.Subject;

/**
 * Created by codebreak on 07/06/16.
 */
public class UpdateAdapter extends BaseAdapter {

    ArrayList myList = new ArrayList();
    LayoutInflater inflater;
    Context context;


    public UpdateAdapter(Context context, ArrayList myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Subject getItem(int position) {
        return (Subject) myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.update_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Subject currentListData = getItem(position);
        mViewHolder.daySubject.setText(currentListData.getName());
        mViewHolder.dayStart.setText(currentListData.getStart_date());
        mViewHolder.dayEnd.setText(currentListData.getEnd_date());
        mViewHolder.dayDoW.setText(currentListData.getDayOfWeek());
        mViewHolder.dayTime.setText(currentListData.getTime());
        mViewHolder.daySquad.setText(currentListData.getSquad());
        mViewHolder.dayClassroom.setText(currentListData.getClassroom());

        return convertView;
    }

    private class MyViewHolder {
        TextView daySubject, dayStart, dayEnd, dayDoW, dayTime, daySquad, dayClassroom;

        public MyViewHolder(View item) {
            daySubject = (TextView) item.findViewById(R.id.day_subject);
            dayStart = (TextView) item.findViewById(R.id.day_start);
            dayEnd = (TextView) item.findViewById(R.id.day_end);
            dayDoW = (TextView) item.findViewById(R.id.day_day);
            dayTime = (TextView) item.findViewById(R.id.day_time);
            daySquad = (TextView) item.findViewById(R.id.day_squad);
            dayClassroom = (TextView) item.findViewById(R.id.day_classroom);
        }
    }
}
