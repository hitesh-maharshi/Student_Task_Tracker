package com.Service;

import com.Model.Task;
import com.Model.User;
import com.Repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task addTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public Task updateTask(Long id, Task updatedTask, User user) {
        Task existingTask = getTaskById(id);

        if (existingTask == null) {
            return null;
        }

        existingTask.setTaskName(updatedTask.getTaskName());
        existingTask.setStudentName(updatedTask.getStudentName());
        existingTask.setDeadline(updatedTask.getDeadline());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setUser(user); // optional if you want to reassign

        return taskRepository.save(existingTask);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public List<Task> getTasksByStudentName(String name) {
        return taskRepository.findByStudentName(name);
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }


    public List<Task> getTasksByStudentNameAndStatus(String name, String status) {
        return taskRepository.findByUserNameAndStatus(name, status);
    }

    //  Your new method to handle status update
    public void updateTaskStatus(Long taskId, String newStatus, User currentUser) {
        Task existingTask = getTaskById(taskId);

        if (existingTask == null) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        // Optional: restrict to student's own tasks
        if (!existingTask.getStudentName().equals(currentUser.getName())) {
            throw new RuntimeException("You are not authorized to update this task.");
        }

        existingTask.setStatus(newStatus);
        taskRepository.save(existingTask);
    }

    public void save(Task task) {
        taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
}
