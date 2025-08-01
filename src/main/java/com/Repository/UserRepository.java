package com.Repository;

import com.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT n.name FROM User n WHERE n.role = 'Student'")
    List<String> findAllNames();

    boolean existsByEmail(String Email);
}