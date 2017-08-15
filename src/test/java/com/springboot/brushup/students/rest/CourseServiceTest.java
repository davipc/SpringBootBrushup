package com.springboot.brushup.students.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.springboot.brushup.students.domain.Course;
import com.springboot.brushup.students.repository.CourseRepository;
import com.springboot.brushup.students.rest.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class CourseServiceTest {

	private static final Course COURSE_DEFAULT_1 = new Course.Builder().id(1).name("aCourse").build();
	private static final Course COURSE_DEFAULT_2 = new Course.Builder().id(2).name("bCourse").build();
	
	@InjectMocks
	private CourseService courseService;
	
	@Mock
	private CourseRepository courseRepository;
	
	/*******************************************************************************************************************************/
	/***   Get Course tests                                                                                                     ***/
	/*******************************************************************************************************************************/

	@Test
	public void testGetCourseNotFound() {
		// setup the mock repository
		given(courseRepository.findOne(anyInt())).willReturn(null);
		
		// make the service call
		Throwable thrown = catchThrowable(() -> { courseService.getCourse(COURSE_DEFAULT_1.getId()); } );
		assertThat(thrown).isNotNull().isInstanceOf(NotFoundException.class).hasMessageContaining("No courses found");
	}
	
	@Test
	public void testGetUserOneFound() {
		// setup the mock repository
		given(courseRepository.findOne(anyInt())).willReturn(COURSE_DEFAULT_1);
		
		// make the service call
		try {
			assertThat(courseService.getCourse(COURSE_DEFAULT_1.getId())).isEqualTo(COURSE_DEFAULT_1);
		} catch (NotFoundException e) {
			fail("Error testing getCourse: " + e.getMessage());
		}
	}

	/*******************************************************************************************************************************/
	/***   Get All Courses tests                                                                                                ***/
	/*******************************************************************************************************************************/
	
	@Test
	public void testGetAllCoursesNoneFound() {
		// setup the mock repository
		given(courseRepository.findAll()).willReturn(new ArrayList<Course>());

		// no need to check for null, isEmpty() checks that too
		assertThat(courseService.getAllCourses()).isEmpty();
	}

	@Test
	public void testGetAllCoursesOneFound() {
		// setup the mock repository
		given(courseRepository.findAll()).willReturn(Arrays.asList(new Course[]{COURSE_DEFAULT_1}));

		// no need to check for null, isEmpty() checks that too
		assertThat(courseService.getAllCourses()).containsExactly(COURSE_DEFAULT_1);
	}

	@Test
	public void testGetAllCoursesTwoFound() {
		// setup the mock repository
		given(courseRepository.findAll()).willReturn(Arrays.asList(new Course[]{COURSE_DEFAULT_1, COURSE_DEFAULT_2}));

		// no need to check for null, isEmpty() checks that too
		assertThat(courseService.getAllCourses()).containsExactly(COURSE_DEFAULT_1, COURSE_DEFAULT_2);
	}
	
	/*******************************************************************************************************************************/
	/***   Create Courses tests                                                                                                 ***/
	/*******************************************************************************************************************************/
	
	@Test
	public void testCreateCourseWithId() {
		// setup the mock repository
		given(courseRepository.save(Matchers.any(Course.class))).willReturn(null);
		
		Throwable thrown = catchThrowable(() -> courseService.createCourse(COURSE_DEFAULT_1));
		assertThat(thrown).isNotNull().isInstanceOf(IllegalArgumentException.class).hasMessage("The ID should not be provided when creating a new course");
	}

	// column enforcement (not null, unique, etc) needs to be tested by the Integration tests, 
	// as we cannot spy the auto-generated spring jpa repository and there is no way to validate 
	// the entity annotations (like @Column(nullable=false)) in here
	/** 
	@Test
	public void testCreateCourseNoName() {
		// can't use this as the repository is actually an interface (Spring creates the actual implementation class internally)
		//given(studentRepository.save(Matchers.any(Course.class))).willCallRealMethod();
		
		Throwable thrown = catchThrowable(() -> courseService.createCourse(new Course.Builder(COURSE_DEFAULT_1).id(null).name(null).build()));
		assertThat(thrown).isNotNull().isInstanceOf(IllegalArgumentException.class).hasMessage("The ID should not be provided when creating a new student");
	}

	@Test
	public void testCreateCourseRepeatedName() {
	}
	**/
	
	@Test
	public void testCreateCourseOK() {
		Course toCreate = new Course.Builder(COURSE_DEFAULT_1).id(null).build();
		given(courseRepository.save(toCreate)).willReturn(COURSE_DEFAULT_1);
		
		Course created = courseService.createCourse(toCreate);
		
		assertThat(created).isEqualTo(COURSE_DEFAULT_1);
	}
	
	/*******************************************************************************************************************************/
	/***   Update Courses tests                                                                                                 ***/
	/*******************************************************************************************************************************/

	@Test
	public void testUpdateCourseBadId() {
		given(courseRepository.findOne(COURSE_DEFAULT_1.getId())).willReturn(null);
		
		Throwable thrown = catchThrowable(() -> courseService.updateCourse(COURSE_DEFAULT_1));
		assertThat(thrown).isNotNull().isInstanceOf(NotFoundException.class).hasMessageContaining("No courses found with ID");
	}

	// column enforcement (not null, unique, etc) needs to be tested by the Integration tests, 
	/** 
	@Test
	public void testUpdateCourseMissingName() {
	}
	**/
	
	@Test
	public void testUpdateCourseOK() {
		given(courseRepository.findOne(COURSE_DEFAULT_1.getId())).willReturn(COURSE_DEFAULT_1);
		given(courseRepository.save(COURSE_DEFAULT_1)).willReturn(COURSE_DEFAULT_1);
		
		Course updated = null;
		try {
			updated = courseService.updateCourse(COURSE_DEFAULT_1);
		} catch (Throwable t) {
			fail("Course update should not have caused an exception", t);
		}
		assertThat(updated).isEqualTo(COURSE_DEFAULT_1);
		
	}
	
	/*******************************************************************************************************************************/
	/***   Delete Courses tests                                                                                                 ***/
	/*******************************************************************************************************************************/

	@Test
	public void testDeleteCourseBadId() {
		given(courseRepository.findOne(COURSE_DEFAULT_1.getId())).willReturn(null);

		Throwable thrown = catchThrowable(() -> courseService.removeCourse(COURSE_DEFAULT_1.getId()));
		assertThat(thrown).isNotNull().isInstanceOf(NotFoundException.class).hasMessageContaining("No courses found with ID");
	}
	
	@Test
	public void testDeleteCourseOK() {
		given(courseRepository.findOne(COURSE_DEFAULT_1.getId())).willReturn(COURSE_DEFAULT_1);
		Throwable thrown = catchThrowable(() -> courseService.removeCourse(COURSE_DEFAULT_1.getId()));
		assertThat(thrown).describedAs("Course deletion with a valid ID should not have caused an exception").isNull();
	}
	
}
