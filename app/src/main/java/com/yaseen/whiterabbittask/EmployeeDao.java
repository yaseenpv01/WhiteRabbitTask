package com.yaseen.whiterabbittask;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yaseen.whiterabbittask.Room.Employee;

import java.util.List;

@Dao
public interface EmployeeDao {

    @Query("SELECT * FROM task")
    List<Employee> getAll();

    @Insert
    void insert(Employee employee);


}
