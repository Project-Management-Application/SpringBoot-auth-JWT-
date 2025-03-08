package com.midou.tutorial.student.services;

import com.midou.tutorial.student.entities.Student;
import com.midou.tutorial.student.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Optional<Student> findById(Long userId) {
        return studentRepository.findById(userId);
    }
}
