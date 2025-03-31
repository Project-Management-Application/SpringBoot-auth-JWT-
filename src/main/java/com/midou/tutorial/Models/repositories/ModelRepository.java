package com.midou.tutorial.Models.repositories;

import com.midou.tutorial.Models.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
}