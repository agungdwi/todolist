package com.example.todolist.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TasksModel extends TaskId{
    private String task;
    private Date due;
    private int status;

    public String getTask() {
        return task;
    }

    public Date getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }

    public String getDueUpdate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, MMM dd, yyyy");
        String due = dateFormat.format(getDue());
        return due;
    }

    @Override
    public String toString() {
        return "TasksModel{" +
                "task='" + task + '\'' +
                ", due=" + due +
                ", status=" + status +
                '}';
    }

    public String dateToString(){
        String due = "";
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, MMM dd, yyyy");

        if(getDue().equals(today)){
            due = "Today";
        }else if (getDue().before(today)){
            due = "Overdue";
        }else if (getDue().equals(tomorrow)){
            due = "Tomorrow";
        }else{
            due = dateFormat.format(getDue());
        }
        return due;


    }
}
