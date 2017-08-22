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
import com.github.springtestdbunit.annotation.ExpectedDatabase;
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

public class CourseServiceIT {
	
	protected static final String DATASET_MULTIPLE = "classpath:datasets/courses_multiple.xml";
	protected static final String DATASET_SINGLE = "classpath:datasets/courses_single.xml";
	protected static final String DATASET_SINGLE_WITH_STUDENTS = "classpath:datasets/courses_single_with_students.xml";
	protected static final String DATASET_SINGLE_WITH_STUDENTS_AFTER_DELETE = "classpath:datasets/courses_single_with_students_after_delete.xml";	
	protected static final String DATASET_SINGLE_CREATED = "classpath:datasets/courses_single_created.xml";
	protected static final String DATASET_SINGLE_CHANGED = "classpath:datasets/courses_single_changed.xml";
	protected static final String DATASET_EMPTY = "classpath:datasets/empty.xml";
	
	private static Course COURSE_1 = Course.builder().id(-1).name("Rest APIs").build();
	private static Course COURSE_2 = Course.builder().id(-2).name("DB Unit").build();
	private static Course COURSE_3 = Course.builder().id(-3).name("Docker").build();
	private static Course COURSE_4 = Course.builder().id(-4).name("ReactJS").build();
	
	private static String TOO_BIG_NAME = "This Name Is Too Long For This Field In The Database As It Has More Than 50 Characters";
	
	@LocalServerPort
	private Integer serverPort;
	
	@Before
	public void setup() {
		RestAssured.port = serverPort;
	}

	/*********************************************************************************************/
	/** Tests - Get All                                                                         **/
	/*********************************************************************************************/
	
	@DatabaseSetup(CourseServiceIT.DATASET_EMPTY)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_EMPTY})
	@Test
	public void testGetAllCoursesNoneFound() {
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
			
		assertThat(courses).isEmpty();
	}

	@DatabaseSetup(CourseServiceIT.DATASET_SINGLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_SINGLE})
	@Test
	public void testGetAllCoursesOneFound() {
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
			
		assertThat(courses).containsExactly(COURSE_1);
	}
	
	@DatabaseSetup(CourseServiceIT.DATASET_MULTIPLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_MULTIPLE})
	@Test
	public void testGetAllCoursesMultipleFound() {
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
	
	/*********************************************************************************************/
	/** Tests - Get                                                                             **/
	/*********************************************************************************************/
	
	@DatabaseSetup(CourseServiceIT.DATASET_EMPTY)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_EMPTY})
	@Test
	public void testGetCourseNotFound() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get(RestPaths.COURSES + "/" + COURSE_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	
	@DatabaseSetup(CourseServiceIT.DATASET_SINGLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_SINGLE})
	@Test
	public void testGetCourseFound() {
		Course course = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.get(RestPaths.COURSES + "/" + COURSE_1.getId())
					.as(Course.class);
		
		assertThat(course).isEqualTo(COURSE_1);
	}

	/*********************************************************************************************/
	/** Tests - Create                                                                          **/
	/*********************************************************************************************/

	@Test
	public void testCreateCourseEmpty() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.post(RestPaths.COURSES)
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	public void testCreateCourseWithId() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(COURSE_1)
			.post(RestPaths.COURSES)
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testCreateCourseTooLongName() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(Course.builder().id(null).name(TOO_BIG_NAME).build())
			.post(RestPaths.COURSES)
		.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@ExpectedDatabase(DATASET_SINGLE_CREATED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_SINGLE_CREATED})
	@Test
	public void testCreateCourseOK() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(COURSE_1.toBuilder().id(null).build())
			.post(RestPaths.COURSES)
		.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value());
	}
	
	/*********************************************************************************************/
	/** Tests - Update                                                                          **/
	/*********************************************************************************************/
	
	@Test
	public void testUpdateCourseEmpty() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.put(RestPaths.COURSES)
		.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testUpdateCourseNullId() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(COURSE_1.toBuilder().id(null).build())
			.put(RestPaths.COURSES)
		.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@DatabaseSetup(DATASET_SINGLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=DATASET_SINGLE)
	@Test
	public void testUpdateCourseInvalidId() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(COURSE_1.toBuilder().id(0).build())
			.put(RestPaths.COURSES)
		.then()
			.log().all()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@DatabaseSetup(DATASET_SINGLE)
	@ExpectedDatabase(DATASET_SINGLE_CHANGED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=DATASET_SINGLE)
	@Test
	public void testUpdateCourseOK() {
		Course updated = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(COURSE_1.toBuilder().name("Changed name").build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.log().all()
				.put(RestPaths.COURSES)
				.as(Course.class);
		
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", COURSE_1.getId());
	}
	
	
	/*********************************************************************************************/
	/** Tests - Delete                                                                          **/
	/*********************************************************************************************/

	@DatabaseSetup(CourseServiceIT.DATASET_EMPTY)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {CourseServiceIT.DATASET_EMPTY})
	@Test
	public void testDeleteCourseNotFound() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete(RestPaths.COURSES + "/" + COURSE_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@DatabaseSetup(CourseServiceIT.DATASET_SINGLE)
	@ExpectedDatabase(CourseServiceIT.DATASET_EMPTY)
	@Test
	public void testDeleteCourseFoundWithoutStudents() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete(RestPaths.COURSES + "/" + COURSE_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}
	
	@DatabaseSetup(CourseServiceIT.DATASET_SINGLE_WITH_STUDENTS)
	@ExpectedDatabase(CourseServiceIT.DATASET_SINGLE_WITH_STUDENTS_AFTER_DELETE)
	@Test
	public void testDeleteCourseFoundWithStudents() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete(RestPaths.COURSES + "/" + COURSE_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	
}
