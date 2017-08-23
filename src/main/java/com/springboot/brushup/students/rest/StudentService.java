package com.springboot.brushup.students.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.brushup.students.domain.Student;
import com.springboot.brushup.students.repository.StudentRepository;
import com.springboot.brushup.students.rest.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
public class StudentService {
	@Autowired
	private StudentRepository students;
	
	// no @ResponseBody needed as @RestController does that
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public Student getStudent(@PathVariable Integer id) 
	throws NotFoundException {
		log.debug("Finding student with id " + id);
		
		Student student = null;
		
		student = students.findOne(id);
		
		if (student == null) {
			String msg = "No students found with ID " + id; 
			log.debug(msg);
			throw new NotFoundException(msg);
		} 		
		
		log.debug("Finished finding student with id " + id);
		
		return student;
	}

	@RequestMapping(method=RequestMethod.GET)
	public List<Student> getAllStudents() {
		log.debug("Finding all students");
		
		List<Student> allStudents = students.findAll();
		
		log.debug("Finished finding all students");
		
		return allStudents;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Student createStudent(@RequestBody Student student) {
		log.debug("Creating student " + student);
		
		if (student.getId() != null) {
			String msg = "The ID should not be provided when creating a new student"; 
			log.debug(msg);
			throw new IllegalArgumentException(msg);
		}

		// will not save the associated courses as the course is the owner of the relationship  
		Student result = students.save(student);
		
		log.debug("Finished creating " + result);
		
		return result;
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public Student updateStudent(@RequestBody Student student) 
	throws NotFoundException, IllegalArgumentException {
		log.debug("Updating student " + student);
		
		if (student.getId() == null) {
			String msg = "No ID provided for student to update"; 
			log.debug(msg);
			throw new IllegalArgumentException();
		}
		
		Student currentStudent = students.findOne(student.getId());
		
		if (currentStudent == null) {
			String msg = "No students found with ID " + student.getId(); 
			log.debug(msg);
			throw new NotFoundException(msg);
		}

		// will not save the associated courses as the course is the owner of the relationship  
		Student result = students.save(student);
		
		log.debug("Finished updating " + student);
		
		return result;
	}

	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeStudent(@PathVariable("id") Integer id) 
	throws NotFoundException {
		Student current = students.findOne(id);
		
		if (current == null) {
			String msg = "No students found with ID " + id; 
			log.debug(msg);
			throw new NotFoundException(msg);
		}
		
		try {
			// since students are the owners in the student x course relationships, 
			// deleting the student will also cause the relationships to be deleted
			students.delete(current);
		} catch (DataIntegrityViolationException e) {
			String msg = "Could not delete student with ID " + id + ": " + e.getMessage(); 
			log.debug(msg, e);
			throw new IllegalArgumentException(msg, e);
		}
	}
}
