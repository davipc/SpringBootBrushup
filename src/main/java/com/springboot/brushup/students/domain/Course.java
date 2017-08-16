package com.springboot.brushup.students.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// needed for spring
@NoArgsConstructor
// needed since the previous constructor was included
@AllArgsConstructor
@Builder(toBuilder=true)

@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
public class Course {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Integer id;

	@Column(nullable=false, unique=true)
	private String name;
	
	@JsonIgnore
	@ManyToMany(mappedBy="courses")
	private Set<Student> students;
	
	public void addStudent(Student student) {
		if (students == null) {
			students = new HashSet<>();
		}
		students.add(student);
	}
}
