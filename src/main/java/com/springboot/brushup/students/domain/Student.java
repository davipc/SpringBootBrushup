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
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
//needed for spring
@NoArgsConstructor
//needed since the previous constructor was included
@AllArgsConstructor
@Builder(toBuilder=true)

@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable=false, unique=true)
	@NotNull
	private String name;

	// @Singular will cause the generated builder to come with a method for adding individual course objects to the collection 
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
