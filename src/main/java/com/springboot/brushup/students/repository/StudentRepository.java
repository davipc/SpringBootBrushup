package com.springboot.brushup.students.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.brushup.students.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Integer>{
	public List<Student> findByName(String studentName);
}
