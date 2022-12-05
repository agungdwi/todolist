package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.todolist.Adapter.MainAdapter;
import com.example.todolist.Adapter.TasksAdapter;
import com.example.todolist.Model.TasksModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  OnDialogCloseListener{

    private RecyclerView rView;
    private FloatingActionButton fab;
    private TextView dayNameTv, dateTv;
    private FirebaseFirestore firestore;
    private MainAdapter mainAdapter;
 //   private TasksAdapter adapter;
    private Calendar c;
    private Date Today;
    private Date Tomorrow;
    private List<TasksModel> list;
    private List<Section> sectionList;
    private Query query;
    private ListenerRegistration listenerRegistration;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        rView = findViewById(R.id.rView);
        fab = findViewById(R.id.floatingActionButton);
        dayNameTv = findViewById(R.id.dayNameTv);
        dateTv = findViewById(R.id.dateTv);
        firestore = FirebaseFirestore.getInstance();
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Today = c.getTime();
        c.add(Calendar.DATE, 1);
        Tomorrow = c.getTime();


        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTask.newInstance().show(getSupportFragmentManager(), AddTask.TAG);
            }
        });

        getTodayDate();




        list = new ArrayList<>();
        sectionList = new ArrayList<>();

        mainAdapter = new MainAdapter(sectionList,MainActivity.this);

        rView.setAdapter(mainAdapter);
        //rView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


//        System.out.println(list);
//        System.out.println(sectionList);

        showData();



    }

    private void getTodayDate(){
        SimpleDateFormat dayFormat = new SimpleDateFormat("EE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        dayNameTv.setText(dayFormat.format(Today));
        dateTv.setText(dateFormat.format(Today));
    }



    private void showData(){
        query = firestore.collection("tasks").orderBy("due", Query.Direction.ASCENDING);


        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if(documentChange.getType() == DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();

                        TasksModel tasksModel = documentChange.getDocument().toObject(TasksModel.class).withId(id);
                        if(tasksModel.getDue() != null){
                            list.add(tasksModel);
                        }

                        System.out.println(tasksModel);





                        mainAdapter.notifyDataSetChanged();




                    }

                }

                listenerRegistration.remove();
//                System.out.println(sectionList);


                initData(list);




            }

        });

    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        sectionList.clear();
        getTodayDate();
        list.clear();
        showData();
        mainAdapter.notifyDataSetChanged();

    }

    public void check(){
//        sectionList.clear();
//        list.clear();
//        showData();
        mainAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        sectionList.clear();
//        list.clear();
//        showData();
//        mainAdapter.notifyDataSetChanged();
//    }


    @Override
    protected void onPause() {
        super.onPause();
        getTodayDate();
        sectionList.clear();
        list.clear();

        showData();
        mainAdapter.notifyDataSetChanged();
    }

    private void initData(List<TasksModel> list){
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);

//        Date today = c.getTime();
//        c.add(Calendar.DATE, 1);
//        Date tomorrow = c.getTime();

        String sectionToday = "Today";
        String sectionTomorow = "Tomorrow";
        String sectionFuture= "Future";
        String sectionOverdue = "Overdue";
        List<TasksModel> todayItem = new ArrayList<>();
        List<TasksModel> tomorrowItem = new ArrayList<>();
        List<TasksModel> futureItem = new ArrayList<>();
        List<TasksModel> overdueItem = new ArrayList<>();




        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getDue() != null) {
                if (list.get(i).getDue().equals(Today)) {

                    todayItem.add(list.get(i));

                } else if (list.get(i).getDue().equals(Tomorrow)) {

                    tomorrowItem.add(list.get(i));

                } else if (list.get(i).getDue().before(Today)) {
                    overdueItem.add(list.get(i));
                } else {

                    futureItem.add(list.get(i));

                }
            }

        }


        sectionList.add(new Section(sectionOverdue,overdueItem));
        sectionList.add(new Section(sectionToday,todayItem));
        sectionList.add(new Section(sectionTomorow,tomorrowItem));
        sectionList.add(new Section(sectionFuture,futureItem));


//        if(overdueItem.size() != 0){
//            sectionList.add(new Section(sectionOverdue,overdueItem));
//        }
//        if(todayItem.size() != 0){
//            sectionList.add(new Section(sectionToday,todayItem));
//        }
//
//        if(tomorrowItem.size() != 0){
//            sectionList.add(new Section(sectionTomorow,tomorrowItem));
//        }
//        if(futureItem.size() != 0){
//            sectionList.add(new Section(sectionFuture,futureItem));
//        }

        Log.d(TAG, "test: "+ list);

        System.out.println(Today + "---------" + Tomorrow);


    }
}