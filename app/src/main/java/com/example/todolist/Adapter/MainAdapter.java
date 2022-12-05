package com.example.todolist.Adapter;

import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.AddTask;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.TasksModel;
import com.example.todolist.R;
import com.example.todolist.Section;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{

    private List<Section> sectionList;
    private MainActivity activity;


    public MainAdapter(){

    }

    public MainAdapter(List<Section> sectionList, MainActivity activity) {
        this.sectionList = sectionList;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_view,parent,false);
        return  new MyViewHolder(view);
    }

    public void delete(int position){
        sectionList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MyViewHolder holder, int position) {

        Section section = sectionList.get(position);
        String sectionName = section.getSectionName();
        List<TasksModel> tasks = section.getTaskList();

        System.out.println(sectionList.get(position));



        if(sectionName.equals("Overdue")){
            holder.sectionTv.setTextColor(Color.RED);
        }
        holder.sectionTv.setText(sectionName);

        TasksAdapter tasksAdapter = new TasksAdapter(activity,tasks);
        holder.rViewChild.setAdapter(tasksAdapter);

        if(tasksAdapter.getItemCount() <= 0){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }else{
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }





    }



    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView sectionTv;
        RecyclerView rViewChild;
        CardView cardView;
 //       FloatingActionButton fab;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionTv = itemView.findViewById(R.id.sectionText);
            rViewChild = itemView.findViewById(R.id.rViewChild);
            cardView = itemView.findViewById(R.id.sectionCd);
//            fab = itemView.findViewById(R.id.rViewChild);


        }
    }


}
