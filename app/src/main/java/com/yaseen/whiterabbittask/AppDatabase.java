package com.yaseen.whiterabbittask;

import androidx.room.Database;

import com.yaseen.whiterabbittask.Room.Employee;

@Database(entities = {Employee.class} ,  version = 1)

public abstract class AppDatabase {

    public abstract EmployeeDao employeeDao();

}
