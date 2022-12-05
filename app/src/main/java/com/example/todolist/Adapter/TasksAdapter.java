package com.example.todolist.Adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.AddTask;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.TasksModel;
import com.example.todolist.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    private List<TasksModel> tasksList;
    private MainActivity activity;
    private MainAdapter mainAdapter = new MainAdapter();
    private FirebaseFirestore firestore;


    public TasksAdapter(MainActivity mainActivity, List<TasksModel> tasksList){
        this.tasksList = tasksList;
        activity = mainActivity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.tasks, parent, false);
        firestore = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }

//    public String dateToString(Date date, int position){
//        TasksModel tasksModel = tasksList.get(position);
//        String due = "";
//        Calendar c = Calendar.getInstance();
//
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//
//        Date today = c.getTime();
//        c.add(Calendar.DATE, 1);
//        Date tomorrow = c.getTime();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//        if(tasksModel.getDue().equals(today)){
//            due = "Today";
//        }else if (tasksModel.getDue().before(today)){
//            due = "Overdue";
//        }else if (tasksModel.getDue().equals(tomorrow)){
//            due = "Tomorrow";
//        }else{
//            due = dateFormat.format(tasksModel.getDue());
//        }
//        return due;
//
//    }

    public void deleteTask(int position){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TasksModel tasksModel = tasksList.get(position);

                firestore.collection("tasks").document(tasksModel.TaskId).delete();
                tasksList.remove(position);
                notifyItemRemoved(position);
                activity.check();
            }
        },1000);



    }

    public void editTask(int position){
        TasksModel tasksModel = tasksList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task", tasksModel.getTask());
        bundle.putString("due", tasksModel.getDueUpdate());
        bundle.putString("id", tasksModel.TaskId);

        AddTask addTask = new AddTask();
        addTask.setArguments(bundle);
        addTask.show(activity.getSupportFragmentManager(), AddTask.TAG);



    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TasksModel tasksModel = tasksList.get(position);
        holder.taskText.setText(tasksModel.getTask());



//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        String due = dateFormat.format(tasksModel.getDue());
//
//
//
//        if(tasksModel.getDue().equals(today)){
//            holder.dueDate.setText("Today");
//        }else if (tasksModel.getDue().after(today)){
//            holder.dueDate.setTextColor(Color.RED);
//            holder.dueDate.setText("Overdue");
//        }else{
//            holder.dueDate.setText(due);
//        }

        String due = tasksModel.dateToString();
        holder.dueDate.setText(due);



        if(due.equals("Overdue")){
            holder.dueDate.setText(tasksModel.getDueUpdate());
            holder.dueDate.setTextColor(Color.RED);
        }

        holder.checkBox.setChecked(toBoolean(tasksModel.getStatus()));


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    firestore.collection("task").document(tasksModel.TaskId).update("status", 1);
                    deleteTask(holder.getAdapterPosition());
                    mainAdapter.notifyDataSetChanged();

                }else{
                    firestore.collection("task").document(tasksModel.TaskId).update("status", 0);
                }
            }
        });

        holder.taskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTask(holder.getAdapterPosition());

            }
        });

    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView dueDate;
        CheckBox checkBox;
        CardView taskCard;
        TextView taskText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dueDate = itemView.findViewById(R.id.due_date);
            checkBox = itemView.findViewById(R.id.checkbox);
            taskCard = itemView.findViewById(R.id.taskCard);
            taskText = itemView.findViewById(R.id.task);


        }
    }


}
