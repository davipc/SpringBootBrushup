package com.springboot.brushup.students.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable=false, unique=true)
	@NotNull
	private String name;

	@ManyToMany
	@JoinTable(name = "COURSE_STUDENT", 
				joinColumns = @JoinColumn(name = "STUD_ID", referencedColumnName = "ID"), 
				inverseJoinColumns = @JoinColumn(name = "COURSE_ID", referencedColumnName = "ID")
	)
	private Set<Course> courses;

	@Column(name = "START_DT")
	private Timestamp startDt;
	
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

	public Timestamp getStartDt() {
		return startDt;
	}

	public void setStartDt(Timestamp startDt) {
		this.startDt = startDt;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

	public Set<Course> getCourses() {
		return courses;
	}

	public void addCourse(Course course) {
		if (courses == null) {
			courses = new HashSet<>();
		}
		courses.add(course);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Student: {")
			.append("ID: ").append(id).append(", ")
			.append("Name: ").append(name).append(", ")
			.append("StartDt: ").append(startDt).append(", ");
		StringJoiner sj = new StringJoiner(",");
		if (courses != null) {
			for (Course course: courses) {
				sj.add(course.getName());
			}
		}
		sb.append("Courses: {").append(sj.toString()).append("}");
		sb.append("}");
		
		return sb.toString();
	}
	
	public Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private Student student = new Student();
		
		public Builder() {
		}
		
		public Builder(Student stud) {
			this.id(stud.getId()).name(stud.getName()).startDt(stud.getStartDt()).courses(stud.getCourses());
		}
		
		public Builder id(Integer id) {
			student.setId(id);
			return this;
		}

		public Builder name(String name) {
			student.setName(name);
			return this;
		}
		
		public Builder startDt(Timestamp startDt) {
			student.setStartDt(startDt);
			return this;
		}

		public Builder courses(Set<Course> courses) {
			student.setCourses(courses);
			return this;
		}

		public Builder addCourse(Course course) {
			student.addCourse(course);
			return this;
		}
		
		public Student build() {
			return student;
		}
	}

}
