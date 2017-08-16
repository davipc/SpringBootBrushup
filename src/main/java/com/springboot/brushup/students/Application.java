package com.springboot.brushup.students;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.springboot.brushup.students.domain.Course;
import com.springboot.brushup.students.domain.Student;
import com.springboot.brushup.students.repository.CourseRepository;
import com.springboot.brushup.students.repository.StudentRepository;

@SpringBootApplication
//@EntityScan(basePackages = "com.springboot.brushup.students.domain") // not needed since this main App class is in the base package
//@EnableJpaRepositories //(basePackages = "com.springboot.brushup.students.repository") // not needed since this main App class is in the base package
public class Application implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	private StudentRepository studentRepo;
	
	@Autowired
	private CourseRepository courseRepo;
	

	public static void main(String []args) {
		
		new SpringApplicationBuilder()
			.sources(Application.class)
			// this way we disable the web context (no Tomcat started, etc)
			//.web(false)
			.build().run(args);
	}
	
	@Transactional
	public void run(String... args) {
		printStudents();
		printClasses();
		
		//createEntriesIfNotExist();
	}
	
	private void printStudents() {
		StringBuilder sb = new StringBuilder("Students:").append("\n");
		for (Student student: studentRepo.findAll() ) {
			sb.append(student);
		}
		logger.debug(sb.toString());
	}
	
	private void printClasses() {
		StringBuilder sb = new StringBuilder("Courses:").append("\n");
		for (Course course: courseRepo.findAll() ) {
			sb.append(course);
		}
		logger.debug(sb.toString());
	}
	
	protected void createEntriesIfNotExist() {
		List<Student> foundStudents = studentRepo.findByName("Levi"); 
		Student levi = (foundStudents != null && foundStudents.size() > 0 
						? foundStudents.get(0) 
						: new Student.Builder().name("Levi").startDt(new Timestamp(Instant.now().toEpochMilli())).build());
		
		foundStudents = studentRepo.findByName("Rachel"); 
		Student rachel = (foundStudents != null && foundStudents.size() > 0 
							? foundStudents.get(0) 
							: new Student.Builder().name("Rachel").startDt(new Timestamp(Instant.now().toEpochMilli())).build());
		
		List<Course> foundCourses = courseRepo.findByName("RCPK");  
		Course childcare = (foundCourses != null && foundCourses.size() > 0 
				? foundCourses.get(0) 
				: new Course.Builder().name("RCPK").build());
		
		foundCourses = courseRepo.findByName("Atlassian");  
		Course atlassian = (foundCourses != null && foundCourses.size() > 0 
				? foundCourses.get(0) 
				: new Course.Builder().name("Atlassian").build());
		
		childcare.addStudent(levi);
		// will not cause the student to be added for childcare, as the Student is the owner of the join relationship
		courseRepo.save(childcare);

		atlassian.addStudent(rachel);
		// will not cause the student to be added for atlassian, as the Student is the owner of the join relationship
		courseRepo.save(atlassian);

		levi.addCourse(childcare);
		studentRepo.save(levi);
		
		rachel.addCourse(atlassian);
		studentRepo.save(rachel);
	}
}
