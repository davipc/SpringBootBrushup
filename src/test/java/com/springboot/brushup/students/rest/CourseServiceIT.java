package com.springboot.brushup.students.rest;

import static org.assertj.core.api.Assertions.*;
import static com.jayway.restassured.RestAssured.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.springboot.brushup.students.domain.Course;
import com.springboot.brushup.students.rest.constants.RestPaths;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({ 
	DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    DbUnitTestExecutionListener.class }
)
@DatabaseSetup(CourseServiceIT.DATASET)
@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET})

public class CourseServiceIT {
	
	//private static Logger logger = LoggerFactory.getLogger(CourseServiceIT.class);
	
	protected static final String DATASET = "classpath:datasets/courses.xml";
	
	private static Course COURSE_1 = new Course.Builder().id(-1).name("Rest APIs").build();
	private static Course COURSE_2 = new Course.Builder().id(-2).name("DB Unit").build();
	private static Course COURSE_3 = new Course.Builder().id(-3).name("Docker").build();
	private static Course COURSE_4 = new Course.Builder().id(-4).name("ReactJS").build();
	
	@LocalServerPort
	private Integer serverPort;
	
	@Before
	public void setup() {
		RestAssured.port = serverPort;
	}
	
	@Test
	public void testGetAllCourses() {
		Course[] courses = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.get(RestPaths.COURSES)
				.as(Course[].class);
			
		assertThat(courses).containsExactlyInAnyOrder(new Course[]{COURSE_1, COURSE_2, COURSE_3, COURSE_4});
	}
}
