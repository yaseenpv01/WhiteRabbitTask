package com.yaseen.whiterabbittask;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yaseen.whiterabbittask.Room.Employee;

@Database(entities = {Employee.class} ,  version = 1)

public abstract class AppDatabase extends RoomDatabase {

    public abstract EmployeeDao employeeDao();

}
