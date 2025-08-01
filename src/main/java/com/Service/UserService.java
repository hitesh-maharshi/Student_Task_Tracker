package com.Service;

import com.Model.Task;
import com.Model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findByEmail(String email);
    List<String> getName();
    List<Task> getTasksByUser(User user);
}