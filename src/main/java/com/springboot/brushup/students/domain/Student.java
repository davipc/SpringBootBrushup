package com.springboot.brushup.students.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

//@Data

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
// can't let equals and hashcode use the courses set, otherwise the cycle will cause an overflow 
@EqualsAndHashCode(exclude="courses")

@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
public class Student {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Integer id;

	@Column(nullable=false, unique=true, length=50)

	// the 2 annotations below cause a javax.validation.ConstraintViolationException to be thrown on validation during save, instead of a DB exception
	// they can be used instead of the @Column annotation attributes above ONLY if Hibernate is the ORM in use, in which case they are also used to generate the DDL
	//@NotNull
	//@Max(50) 
	
	private String name;

	@Singular
	@ManyToMany
	@JoinTable(name = "COURSE_STUDENT", 
				joinColumns = @JoinColumn(name = "STUD_ID", referencedColumnName = "ID"), 
				inverseJoinColumns = @JoinColumn(name = "COURSE_ID", referencedColumnName = "ID")
	)
	private Set<Course> courses;

	@Column(name = "START_DT")
	private Timestamp startDt;

	public void addCourse(Course course) {
		if (courses == null) {
			courses = new HashSet<>();
		}
		courses.add(course);
	}
}
