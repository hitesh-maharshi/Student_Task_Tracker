package com.Repository;

import com.Model.Task;
import com.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByStudentName(String name);
    List<Task> findByStudentNameAndStatus(String name, String status);

}