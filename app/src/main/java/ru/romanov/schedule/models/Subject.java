package ru.romanov.schedule.models;

/**
 * Created by codebreak on 07/06/16.
 */
public class Subject {

    private Integer id;
    private String name;
    private String start_date;
    private String end_date;
    private String dayOfWeek;
    private String time;
    private String squad;
    private String classroom;
    private int checked;

    public Subject() {
    }

    public Subject(Integer id, String name, String start_date, String end_date, String dayOfWeek, String time, String squad, String classroom) {
        this.id = id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.squad = squad;
        this.classroom = classroom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSquad() {
        return squad;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setChecked(int id) {
        checked = id;
    }

    public int getChecked() {
        return checked;
    }
}
