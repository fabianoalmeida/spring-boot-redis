package com.example.redis.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.redis.model.Student;

@Repository
public interface StudentRepository extends CrudRepository<Student, String> {}
