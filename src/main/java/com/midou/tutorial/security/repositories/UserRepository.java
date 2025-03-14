package com.midou.tutorial.security.repositories;

import com.midou.tutorial.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT s FROM User s WHERE s.email= ?1")
    Optional<User> findByEmail(String email);
}
