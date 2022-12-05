package com.example.todolist;

import com.example.todolist.Model.TasksModel;

import java.util.List;

public class Section {

    private String sectionName;
    private List<TasksModel> taskList;

    public Section(String sectionName, List<TasksModel> taskList) {
        this.sectionName = sectionName;
        this.taskList = taskList;
    }



    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<TasksModel> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TasksModel> taskList) {
        this.taskList = taskList;
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionName='" + sectionName + '\'' +
                ", taskList=" + taskList +
                '}';
    }
}
