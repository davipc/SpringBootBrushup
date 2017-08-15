package com.springboot.brushup.students.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.brushup.students.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	public List<Course> findByName(String courseName);

}
