package com.springboot.brushup.students.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	public Integer getId() {
		return id;
	}

	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Set<Student> getStudents() {
		return students;
	}

	public void addStudent(Student student) {
		if (students == null) {
			students = new HashSet<>();
		}
		students.add(student);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Course: {")
			.append("ID: ").append(id).append(", ")
			.append("Name: ").append(name).append(", ");
		StringJoiner sj = new StringJoiner(",");
		if (students != null) {
			for (Student student: students) {
				sj.add(student.getName());
			}
		}
		sb.append("Students: {").append(sj.toString()).append("}");
		sb.append("}");
		
		return sb.toString();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Course))
			return false;
		
		Course other = (Course) obj;
		return id == other.getId() && name.equals(other.getName());
	}
	
	public Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private Course course = new Course();
		
		public Builder() {}
		
		public Builder(Course inCourse) {
			this.id(inCourse.getId()).name(inCourse.getName());
		}
		
		public Builder id(Integer id) {
			course.setId(id);
			return this;
		}

		public Builder name(String name) {
			course.setName(name);
			return this;
		}
		
		public Builder addStudent(Student student) {
			course.addStudent(student);
			return this;
		}
		
		public Course build() {
			return course;
		}
	}
	
}
