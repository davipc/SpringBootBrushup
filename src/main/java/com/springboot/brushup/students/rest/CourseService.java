package com.springboot.brushup.students.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.brushup.students.domain.Course;
import com.springboot.brushup.students.domain.Student;
import com.springboot.brushup.students.repository.CourseRepository;
import com.springboot.brushup.students.rest.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
public class CourseService {
	
	@Autowired
	private CourseRepository courses;
	
	// no @ResponseBody needed as @RestController does that
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public Course getCourse(@PathVariable Integer id) 
	throws NotFoundException {
		log.debug("Finding course with id " + id);
		
		Course course = null;
		
		course = courses.findOne(id);
		
		if (course == null) {
			String msg = "No courses found with ID " + id; 
			log.debug(msg);
			throw new NotFoundException(msg);
		} 		
		
		log.debug("Finished finding course with id " + id);
		
		return course;
	}

	@RequestMapping(method=RequestMethod.GET)
	public List<Course> getAllCourses() { 
		log.debug("Finding all courses");
		
		List<Course> allCourses = courses.findAll();
		
		log.debug("Finished finding all courses");
		
		return allCourses;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Course createCourse(@RequestBody Course course) {
		log.debug("Creating course " + course);
		
		if (course.getId() != null) {
			String msg = "The ID should not be provided when creating a new course"; 
			log.debug(msg);
			throw new IllegalArgumentException(msg);
		}

		// will not save the associated courses as the course is the owner of the relationship  
		Course result = courses.save(course);
		
		log.debug("Finished creating " + result);
		
		return result;
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public Course updateCourse(@RequestBody Course course) 
	throws NotFoundException {
		log.debug("Updating course " + course);
		
		if (course.getId() == null) {
			String msg = "No ID provided for course to update"; 
			log.debug(msg);
			throw new IllegalArgumentException();
		}
		
		Course currentCourse = courses.findOne(course.getId());
		
		if (currentCourse == null) {
			String msg = "No courses found with ID " + course.getId(); 
			log.debug(msg);
			throw new NotFoundException(msg);
		}

		// will not save the associated courses as the course is the owner of the relationship  
		Course result = courses.save(course);
		
		log.debug("Finished updating " + course);
		
		return result;
	}

	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeCourse(@PathVariable("id") Integer id) 
	throws NotFoundException {
		Course current = courses.findOne(id);
		
		if (current == null) {
			String msg = "No courses found with ID " + id; 
			log.debug(msg);
			throw new NotFoundException(msg);
		}
		
		try {
			// course is not the owner of the student x course relationship... 
			// need the remove the relationship from the students first 
			if (current.getStudents() != null) {
				for (Student student: current.getStudents()) {
					student.getCourses().remove(current);
				}
			}
			courses.delete(current);
		} catch (DataIntegrityViolationException e) {
			String msg = "Could not delete course with ID " + id + ": " + e.getMessage(); 
			log.debug(msg, e);
			throw new IllegalArgumentException(msg, e);
		}
	}
	
	
	@ExceptionHandler(NotFoundException.class)
	void handleNotFoundException(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.NOT_FOUND.value());
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	void handleIllegalArgumentException(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
}
