package com.springboot.brushup.students.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.springboot.brushup.students.domain.Student;
import com.springboot.brushup.students.rest.constants.RestPaths;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({ 
	DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    DbUnitTestExecutionListener.class }
)

public class StudentServiceIT {
	
	protected static final String DATASET_MULTIPLE = "classpath:datasets/students_multiple.xml";
	protected static final String DATASET_SINGLE = "classpath:datasets/students_single.xml";
	protected static final String DATASET_SINGLE_WITH_SINGLE_COURSE = "classpath:datasets/students_single_with_single_course.xml";
	protected static final String DATASET_SINGLE_WITH_MULTIPLE_COURSES = "classpath:datasets/students_single_with_multiple_courses.xml";
	protected static final String DATASET_SINGLE_WITH_SINGLE_COURSE_AFTER_DELETE = "classpath:datasets/students_single_with_single_course_after_delete.xml";	
	protected static final String DATASET_SINGLE_CREATED = "classpath:datasets/students_single_created.xml";
	protected static final String DATASET_SINGLE_CREATED_SINGLE_COURSE = "classpath:datasets/students_single_created_with_single_course.xml";
	protected static final String DATASET_SINGLE_CREATED_MULTIPLE_COURSES = "classpath:datasets/students_single_created_with_multiple_courses.xml";
	protected static final String DATASET_SINGLE_CHANGED = "classpath:datasets/students_single_changed.xml";
	protected static final String DATASET_SINGLE_WITH_SINGLE_COURSE_CHANGED = "classpath:datasets/students_single_with_single_course_changed.xml";
	protected static final String DATASET_SINGLE_WITH_MULTIPLE_COURSES_CHANGED = "classpath:datasets/students_single_with_multiple_courses_changed.xml";
	protected static final String DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED = "classpath:datasets/students_single_with_multiple_courses_added.xml";
	protected static final String DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED2  = "classpath:datasets/students_single_with_multiple_courses_added2.xml";
	protected static final String DATASET_SINGLE_WITH_MULTIPLE_COURSES_REPLACED2  = "classpath:datasets/students_single_with_multiple_courses_replaced2.xml";
	protected static final String DATASET_EMPTY = "classpath:datasets/empty.xml";
	
	// using deprecated constructor just to facilitate comparing to values in DB dataset files 
	@SuppressWarnings("deprecation")
	protected static Student STUDENT_1 = Student.builder().id(-1).name("Melissa").startDt(new Timestamp(117, 7, 17, 6,16,15,0)).build();
	@SuppressWarnings("deprecation")
	protected static Student STUDENT_2 = Student.builder().id(-2).name("Levi").startDt(new Timestamp(117, 7, 17, 19,20,21,0)).build();
	protected static Student STUDENT_1_WITH_SINGLE_COURSE = STUDENT_1.toBuilder().course(CourseServiceIT.COURSE_1).build();	
	protected static Student STUDENT_1_WITH_COURSES = STUDENT_1.toBuilder().course(CourseServiceIT.COURSE_1).course(CourseServiceIT.COURSE_2).build();	
	
	@LocalServerPort
	private Integer serverPort;
	
	@Before
	public void setup() {
		RestAssured.port = serverPort;
	}

	/*********************************************************************************************/
	/** Tests - Get All                                                                         **/
	/*********************************************************************************************/
	
	@DatabaseSetup(StudentServiceIT.DATASET_EMPTY)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_EMPTY})
	@Test
	public void testGetAllStudentsNoneFound() {
		Student[] students = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.get(RestPaths.STUDENTS)
				.as(Student[].class);
			
		assertThat(students).isEmpty();
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE})
	@Test
	public void testGetAllStudentsOneFoundWithoutCourses() {
		Student[] students = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.get(RestPaths.STUDENTS)
				.as(Student[].class);
			
		assertThat(students).containsExactly(STUDENT_1);
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_SINGLE_COURSE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_WITH_SINGLE_COURSE})
	@Test
	public void testGetAllStudentsOneFoundWithSingleCourse() {
		Student[] students = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.get(RestPaths.STUDENTS)
				.as(Student[].class);
			
		assertThat(students).containsExactly(STUDENT_1_WITH_SINGLE_COURSE);
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES})
	@Test
	public void testGetAllStudentsOneFoundWithMultipleCourses() {
		Student[] students = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.get(RestPaths.STUDENTS)
				.as(Student[].class);
			
		assertThat(students).containsExactly(STUDENT_1_WITH_COURSES);
	}
	
	@DatabaseSetup(StudentServiceIT.DATASET_MULTIPLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_MULTIPLE})
	@Test
	public void testGetAllStudentsMultipleFound() {
		Student[] students = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.get(RestPaths.STUDENTS)
				.as(Student[].class);
			
		assertThat(students).containsExactlyInAnyOrder(new Student[]{STUDENT_1, STUDENT_2});
	}
	
	/*********************************************************************************************/
	/** Tests - Get                                                                             **/
	/*********************************************************************************************/
	
	@DatabaseSetup(StudentServiceIT.DATASET_EMPTY)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_EMPTY})
	@Test
	public void testGetStudentNotFound() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get(RestPaths.STUDENTS + "/" + STUDENT_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	
	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE})
	@Test
	public void testGetStudentFoundWithoutCourses() {
		Student course = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.get(RestPaths.STUDENTS + "/" + STUDENT_1.getId())
					.as(Student.class);
		
		assertThat(course).isEqualTo(STUDENT_1);
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_SINGLE_COURSE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_WITH_SINGLE_COURSE})
	@Test
	public void testGetStudentFoundWithOneCourse() {
		Student course = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.get(RestPaths.STUDENTS + "/" + STUDENT_1_WITH_SINGLE_COURSE.getId())
					.as(Student.class);
		
		assertThat(course).isEqualTo(STUDENT_1_WITH_SINGLE_COURSE);
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES})
	@Test
	public void testGetStudentFoundWithMultipleCourses() {
		Student course = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.get(RestPaths.STUDENTS + "/" + STUDENT_1_WITH_COURSES.getId())
					.as(Student.class);
		
		assertThat(course).isEqualTo(STUDENT_1_WITH_COURSES);
	}
	
	/*********************************************************************************************/
	/** Tests - Create                                                                          **/
	/*********************************************************************************************/

	@Test
	public void testCreateStudentEmpty() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.post(RestPaths.STUDENTS)
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	public void testCreateStudentWithId() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(STUDENT_1)
			.post(RestPaths.STUDENTS)
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testCreateStudentTooLongName() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(Student.builder().id(null).name(CourseServiceIT.TOO_BIG_NAME).startDt(new Timestamp(System.currentTimeMillis())).build())
			.post(RestPaths.STUDENTS)
		.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@DatabaseSetup(DATASET_EMPTY)
	@ExpectedDatabase(DATASET_SINGLE_CREATED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_CREATED})
	// will cause the whole Spring context, including the DB, to be recreated after this test (that's the only way to avoid issues 
	// caused by ID auto-generation - we can't reset sequences in MySQL/MariaDB)
	@DirtiesContext(methodMode=DirtiesContext.MethodMode.AFTER_METHOD)
	@Test
	public void testCreateStudentOKNoCourses() {
		Student created = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(STUDENT_1.toBuilder().id(null).build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.CREATED.value())		
			.when()
				.log().all()
				.post(RestPaths.STUDENTS)
				.as(Student.class);

		assertThat(created).isEqualTo(STUDENT_1.toBuilder().id(1).build());
	}

	@DatabaseSetup(CourseServiceIT.DATASET_SINGLE)
	@ExpectedDatabase(StudentServiceIT.DATASET_SINGLE_CREATED_SINGLE_COURSE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_CREATED_SINGLE_COURSE})
	// will cause the DB to be recreated after this test (that's the only way to avoid issues caused by ID auto-generation - 
	// we can't reset sequences in MySQL/MariaDB)
	@DirtiesContext(methodMode=DirtiesContext.MethodMode.AFTER_METHOD)
	@Test
	public void testCreateStudentOKSingleCourse() {
		Student created = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(STUDENT_1_WITH_SINGLE_COURSE.toBuilder().id(null).build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.CREATED.value())
			.when()
				.log().all()
				.post(RestPaths.STUDENTS)
				.as(Student.class);
		
		assertThat(created).isEqualTo(STUDENT_1_WITH_SINGLE_COURSE.toBuilder().id(1).build());
	}

	@DatabaseSetup(CourseServiceIT.DATASET_MULTIPLE)
	@ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED, value=StudentServiceIT.DATASET_SINGLE_CREATED_MULTIPLE_COURSES)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_SINGLE_CREATED_MULTIPLE_COURSES})
	// will cause the DB to be recreated after this test (that's the only way to avoid issues caused by ID auto-generation - 
	// we can't reset sequences in MySQL/MariaDB)
	@DirtiesContext(methodMode=DirtiesContext.MethodMode.AFTER_METHOD)
	@Test
	public void testCreateStudentOKMultipleCourses() {
		Student created = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(STUDENT_1_WITH_COURSES.toBuilder().id(null).build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.CREATED.value())		
			.when()
				.log().all()
				.post(RestPaths.STUDENTS)
				.as(Student.class);
		
		assertThat(created).isEqualTo(STUDENT_1_WITH_COURSES.toBuilder().id(1).build());
	}
	
	/*********************************************************************************************/
	/** Tests - Update                                                                          **/
	/*********************************************************************************************/

	@Test
	public void testUpdateStudentEmpty() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.put(RestPaths.STUDENTS)
		.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testUpdateStudentNullId() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(STUDENT_1.toBuilder().id(null).build())
			.put(RestPaths.STUDENTS)
		.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@DatabaseSetup(DATASET_SINGLE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=DATASET_SINGLE)
	@Test
	public void testUpdateStudentInvalidId() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.log().all()
			.body(STUDENT_1.toBuilder().id(0).build())
			.put(RestPaths.STUDENTS)
		.then()
			.log().all()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@DatabaseSetup(DATASET_SINGLE)
	@ExpectedDatabase(DATASET_SINGLE_CHANGED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=DATASET_SINGLE)
	@Test
	public void testUpdateStudentNameOKNoCourses() {
		Student updated = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(STUDENT_1.toBuilder().name("Changed name").build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.log().all()
				.put(RestPaths.STUDENTS)
				.as(Student.class);
		
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1.getId());
	}

	@DatabaseSetup(DATASET_SINGLE_WITH_SINGLE_COURSE)
	@ExpectedDatabase(DATASET_SINGLE_WITH_SINGLE_COURSE_CHANGED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=DATASET_SINGLE_WITH_SINGLE_COURSE_CHANGED)
	@Test
	public void testUpdateStudentNameOKWithSingleCourse() {
		Student updated = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(STUDENT_1_WITH_SINGLE_COURSE.toBuilder().name("Changed name").build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.log().all()
				.put(RestPaths.STUDENTS)
				.as(Student.class);
		
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_SINGLE_COURSE.getId());
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_CHANGED, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_CHANGED)
	@Test
	public void testUpdateStudentNameOKWithMultipleCourses() {
		Student updated = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(STUDENT_1_WITH_COURSES.toBuilder().name("Changed name").build())
			.expect()
				.log().all()
				.statusCode(HttpStatus.OK.value())
			.when()
				.log().all()
				.put(RestPaths.STUDENTS)
				.as(Student.class);
		
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_COURSES.getId());
	}

	@DatabaseSetup(value=CourseServiceIT.DATASET_SINGLE, type=DatabaseOperation.INSERT)
	@DatabaseSetup(value=DATASET_SINGLE, type=DatabaseOperation.INSERT)
	@ExpectedDatabase(DATASET_SINGLE_WITH_SINGLE_COURSE)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_SINGLE_COURSE)
	@Test
	public void testUpdateStudentAddCourseFromNoneOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1_WITH_SINGLE_COURSE)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_SINGLE_COURSE.getId());
	}

	@DatabaseSetup(value=CourseServiceIT.DATASET_MULTIPLE, type=DatabaseOperation.INSERT)
	@DatabaseSetup(value=DATASET_SINGLE, type=DatabaseOperation.INSERT)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@Test
	public void testUpdateStudentAddCourseFromOneOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1_WITH_COURSES)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_COURSES.getId());
	}

	@DatabaseSetup(DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED)
	@Test
	public void testUpdateStudentAddCourseFromMultipleOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1_WITH_COURSES.toBuilder().course(CourseServiceIT.COURSE_3).build())
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_COURSES.getId());
	}

	@DatabaseSetup(value=CourseServiceIT.DATASET_MULTIPLE, type=DatabaseOperation.INSERT)
	@DatabaseSetup(value=DATASET_SINGLE, type=DatabaseOperation.INSERT)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@Test
	public void testUpdateStudentAddCoursesFromNoneOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1_WITH_COURSES)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_COURSES.getId());
	}

	@DatabaseSetup(value=CourseServiceIT.DATASET_MULTIPLE, type=DatabaseOperation.INSERT)
	// need to refresh on the next setup as the same course is in both datasets
	@DatabaseSetup(value=StudentServiceIT.DATASET_SINGLE_WITH_SINGLE_COURSE, type=DatabaseOperation.REFRESH)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED)
	@Test
	public void testUpdateStudentAddCoursesFromOneOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1.toBuilder().courses(Arrays.asList(CourseServiceIT.COURSE_1, CourseServiceIT.COURSE_2, CourseServiceIT.COURSE_3)).build())
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1.getId());
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED2, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED2)
	@Test
	public void testUpdateStudentAddCoursesFromMultipleOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1.toBuilder().courses(Arrays.asList(CourseServiceIT.COURSE_1, CourseServiceIT.COURSE_2, CourseServiceIT.COURSE_3, CourseServiceIT.COURSE_4)).build())
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1.getId());
	}

	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_ADDED2)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@Test
	public void testUpdateStudentRemoveCoursesFromMultipleOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1_WITH_COURSES)
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1_WITH_COURSES.getId());
	}

	@DatabaseSetup(value=CourseServiceIT.DATASET_MULTIPLE, type=DatabaseOperation.INSERT)
	@DatabaseSetup(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES, type=DatabaseOperation.REFRESH)
	@ExpectedDatabase(value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_REPLACED2, assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value=StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES_REPLACED2)
	@Test
	public void testUpdateStudentRemoveAndAddCoursesFromMultipleOK() {
		Student updated = 
				given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(STUDENT_1.toBuilder().courses(Arrays.asList(CourseServiceIT.COURSE_3, CourseServiceIT.COURSE_4)).build())
				.expect()
					.log().all()
					.statusCode(HttpStatus.OK.value())
				.when()
					.log().all()
					.put(RestPaths.STUDENTS)
					.as(Student.class);
			
		assertThat(updated).isNotNull().hasFieldOrPropertyWithValue("id", STUDENT_1.getId());
	}
	
	/*********************************************************************************************/
	/** Tests - Delete                                                                          **/
	/*********************************************************************************************/

	@DatabaseSetup(StudentServiceIT.DATASET_EMPTY)
	@DatabaseTearDown(type=DatabaseOperation.DELETE_ALL, value= {StudentServiceIT.DATASET_EMPTY})
	@Test
	public void testDeleteStudentNotFound() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete(RestPaths.STUDENTS + "/" + STUDENT_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE)
	@ExpectedDatabase(StudentServiceIT.DATASET_EMPTY)
	@Test
	public void testDeleteStudentFoundWithoutCourses() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete(RestPaths.STUDENTS + "/" + STUDENT_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}
	
	@DatabaseSetup(StudentServiceIT.DATASET_SINGLE_WITH_MULTIPLE_COURSES)
	@ExpectedDatabase(value=CourseServiceIT.DATASET_MULTIPLE,assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void testDeleteStudentFoundWithCourses() {
		given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete(RestPaths.STUDENTS + "/" + STUDENT_1.getId())
		.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}
}
