package com.example.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Adapter.TasksAdapter;
import com.example.todolist.Model.TasksModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends BottomSheetDialogFragment {

    private TextView setDate;
    private EditText taskEdit;
    private Button saveBtn;
    private FirebaseFirestore firestore;
    private Context context;
    private Date dueDate;
    private String id = "";
    private String dueDateUpdate = "";


    public static  final String TAG = "AddTask";


    public static  AddTask newInstance(){
        return new AddTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDate = view.findViewById(R.id.set_date);
        taskEdit = view.findViewById(R.id.add_edittext);
        saveBtn = view.findViewById(R.id.save_btn);


        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate =false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("due");

            taskEdit.setText(task);
            setDate.setText(dueDateUpdate);


        }

        saveBtn.setEnabled(false);
        saveBtn.setBackgroundColor(Color.GRAY);


        taskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundColor(Color.GRAY);
                }else{
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundColor(Color.GRAY);
                }else{
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(setDate.getText().equals("Set Due Date")){
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundColor(Color.GRAY);
                }

            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();



                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DATE = calendar.get(Calendar.DATE);
                int DAY = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EE");
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
                        Date date = new Date(i - 1900,i1,i2);
                        System.out.println(i);
                        String dayS = dayFormat.format(date);
                        String monthS = monthFormat.format(date);
                        System.out.println(dayS + " " + monthS);
                        i1 = i1 + 1;




                        setDate.setText(dayS+ ", " + monthS + " " + i2 + ", " + i );
//                        dueDate = i2+"/"+ i1 + "/" + i;
                        try {
                            dueDate = new SimpleDateFormat("EE, MMM dd, yyyy").parse(setDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                        if(setDate.getText().equals("Set Due Date") || taskEdit.getText().toString().equals("")){
                            saveBtn.setEnabled(false);
                            saveBtn.setBackgroundColor(Color.GRAY);
                        }else{
                            saveBtn.setEnabled(true);
                            saveBtn.setBackgroundColor(Color.WHITE);
                        }

                    }
                }, YEAR , MONTH, DATE);

                datePickerDialog.show();

            }
        });



        boolean finalIsUpdate = isUpdate;
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = taskEdit.getText().toString();
                String date = setDate.getText().toString();

                if(finalIsUpdate){
                    try {
                        dueDate = new SimpleDateFormat("EE, MMM dd, yyyy").parse(setDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    firestore.collection("tasks").document(id).update("task",task,"due",dueDate);
                    Toast.makeText(context, "Task Updated !!", Toast.LENGTH_SHORT).show();


                }else {


                    if (task.isEmpty()) {
                        Toast.makeText(context, "Task cant be empty !!", Toast.LENGTH_SHORT).show();

                    } else if (date.equals("Set Due Date")) {
                        Toast.makeText(context, "Set Your Due Date !!", Toast.LENGTH_SHORT).show();

                    } else {

                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", task);
                        taskMap.put("due", dueDate);
                        taskMap.put("status", 0);

                        firestore.collection("tasks").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                dismiss();
            }
        });

        }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context =context;

    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof  OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);

        }

    }
}


