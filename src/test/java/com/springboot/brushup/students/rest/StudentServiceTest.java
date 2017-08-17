package com.springboot.brushup.students.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.springboot.brushup.students.domain.Course;
import com.springboot.brushup.students.domain.Student;
import com.springboot.brushup.students.repository.StudentRepository;
import com.springboot.brushup.students.rest.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class StudentServiceTest {

	private static final Course COURSE_DEFAULT_1 = Course.builder().id(1).name("aCourse").build();
	private static final Course COURSE_DEFAULT_2 = Course.builder().id(2).name("bCourse").build();
	
	private static final Student STUDENT_DEFAULT_1 = Student.builder().id(1).name("aStudent").course(COURSE_DEFAULT_1).startDt(new Timestamp(System.currentTimeMillis())).build();
	private static final Student STUDENT_DEFAULT_2 = Student.builder().id(2).name("bStudent").course(COURSE_DEFAULT_2).startDt(new Timestamp(System.currentTimeMillis()-100000)).build();
	
	static {
		// we need to define both ways of the relationship
		COURSE_DEFAULT_1.addStudent(STUDENT_DEFAULT_1);
		COURSE_DEFAULT_2.addStudent(STUDENT_DEFAULT_2);
	}
	
	@InjectMocks
	private StudentService studentService;
	
	@Mock
	private StudentRepository studentRepository;
	
	/*******************************************************************************************************************************/
	/***   Get Student tests                                                                                                     ***/
	/*******************************************************************************************************************************/

	@Test
	public void testGetStudentNotFound() {
		// setup the mock repository
		given(studentRepository.findOne(anyInt())).willReturn(null);
		
		// make the service call
		Throwable thrown = catchThrowable(() -> { studentService.getStudent(STUDENT_DEFAULT_1.getId()); } );
		assertThat(thrown).isNotNull().isInstanceOf(NotFoundException.class).hasMessageContaining("No students found");
	}
	
	@Test
	public void testGetUserOneFound() {
		// setup the mock repository
		given(studentRepository.findOne(anyInt())).willReturn(STUDENT_DEFAULT_1);
		
		// make the service call
		try {
			assertThat(studentService.getStudent(STUDENT_DEFAULT_1.getId())).isEqualTo(STUDENT_DEFAULT_1);
		} catch (NotFoundException e) {
			fail("Error testing getStudent: " + e.getMessage());
		}
	}

	/*******************************************************************************************************************************/
	/***   Get All Students tests                                                                                                ***/
	/*******************************************************************************************************************************/
	
	@Test
	public void testGetAllStudentsNoneFound() {
		// setup the mock repository
		given(studentRepository.findAll()).willReturn(new ArrayList<Student>());

		// no need to check for null, isEmpty() checks that too
		assertThat(studentService.getAllStudents()).isEmpty();
	}

	@Test
	public void testGetAllStudentsOneFound() {
		// setup the mock repository
		given(studentRepository.findAll()).willReturn(Arrays.asList(new Student[]{STUDENT_DEFAULT_1}));

		// no need to check for null, isEmpty() checks that too
		assertThat(studentService.getAllStudents()).containsExactly(STUDENT_DEFAULT_1);
	}

	@Test
	public void testGetAllStudentsTwoFound() {
		// setup the mock repository
		given(studentRepository.findAll()).willReturn(Arrays.asList(new Student[]{STUDENT_DEFAULT_1, STUDENT_DEFAULT_2}));

		// no need to check for null, isEmpty() checks that too
		assertThat(studentService.getAllStudents()).containsExactly(STUDENT_DEFAULT_1, STUDENT_DEFAULT_2);
	}
	
	/*******************************************************************************************************************************/
	/***   Create Students tests                                                                                                 ***/
	/*******************************************************************************************************************************/
	
	@Test
	public void testCreateStudentWithId() {
		// setup the mock repository
		given(studentRepository.save(Matchers.any(Student.class))).willReturn(null);
		
		Throwable thrown = catchThrowable(() -> studentService.createStudent(STUDENT_DEFAULT_1));
		assertThat(thrown).isNotNull().isInstanceOf(IllegalArgumentException.class).hasMessage("The ID should not be provided when creating a new student");
	}

	// column enforcement (not null, unique, etc) needs to be tested by the Integration tests, 
	// as we cannot spy the auto-generated spring jpa repository and there is no way to validate 
	// the entity annotations (like @Column(nullable=false)) in here
	/** 
	@Test
	public void testCreateStudentNoName() {
		// can't use this as the repository is actually an interface (Spring creates the actual implementation class internally)
		//given(studentRepository.save(Matchers.any(Student.class))).willCallRealMethod();
		
		Throwable thrown = catchThrowable(() -> studentService.createStudent(new Student.Builder(STUDENT_DEFAULT_1).id(null).name(null).build()));
		assertThat(thrown).isNotNull().isInstanceOf(IllegalArgumentException.class).hasMessage("The ID should not be provided when creating a new student");
	}

	@Test
	public void testCreateStudentRepeatedName() {
	}
	**/
	
	@Test
	public void testCreateStudentOK() {
		Student toCreate = STUDENT_DEFAULT_1.toBuilder().id(null).build();
		given(studentRepository.save(toCreate)).willReturn(STUDENT_DEFAULT_1);
		
		Student created = studentService.createStudent(toCreate);
		
		assertThat(created).isEqualTo(STUDENT_DEFAULT_1);
	}
	
	/*******************************************************************************************************************************/
	/***   Update Students tests                                                                                                 ***/
	/*******************************************************************************************************************************/

	@Test
	public void testUpdateStudentBadId() {
		given(studentRepository.findOne(STUDENT_DEFAULT_1.getId())).willReturn(null);
		
		Throwable thrown = catchThrowable(() -> studentService.updateStudent(STUDENT_DEFAULT_1));
		assertThat(thrown).isNotNull().isInstanceOf(NotFoundException.class).hasMessageContaining("No students found with ID");
	}

	// column enforcement (not null, unique, etc) needs to be tested by the Integration tests, 
	/** 
	@Test
	public void testUpdateStudentMissingName() {
	}
	**/
	
	@Test
	public void testUpdateStudentOK() {
		given(studentRepository.findOne(STUDENT_DEFAULT_1.getId())).willReturn(STUDENT_DEFAULT_1);
		given(studentRepository.save(STUDENT_DEFAULT_1)).willReturn(STUDENT_DEFAULT_1);
		
		Student updated = null;
		try {
			updated = studentService.updateStudent(STUDENT_DEFAULT_1);
		} catch (Throwable t) {
			fail("Student update should not have caused an exception", t);
		}
		assertThat(updated).isEqualTo(STUDENT_DEFAULT_1);
		
	}
	
	/*******************************************************************************************************************************/
	/***   Delete Students tests                                                                                                 ***/
	/*******************************************************************************************************************************/

	@Test
	public void testDeleteStudentBadId() {
		given(studentRepository.findOne(STUDENT_DEFAULT_1.getId())).willReturn(null);

		Throwable thrown = catchThrowable(() -> studentService.removeStudent(STUDENT_DEFAULT_1.getId()));
		assertThat(thrown).isNotNull().isInstanceOf(NotFoundException.class).hasMessageContaining("No students found with ID");
	}
	
	@Test
	public void testDeleteStudentOK() {
		given(studentRepository.findOne(STUDENT_DEFAULT_1.getId())).willReturn(STUDENT_DEFAULT_1);
		Throwable thrown = catchThrowable(() -> studentService.removeStudent(STUDENT_DEFAULT_1.getId()));
		assertThat(thrown).describedAs("Student deletion with a valid ID should not have caused an exception").isNull();
	}
	
}
