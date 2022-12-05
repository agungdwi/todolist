package com.example.todolist.Model;

import com.google.firebase.firestore.Exclude;

import javax.annotation.Nonnull;

public class TaskId {

    @Exclude
    public String TaskId;

    public <T extends  TaskId> T withId(@Nonnull final String id){
       this.TaskId = id;
       return  (T) this;
    }
}
