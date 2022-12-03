package com.driver.services;

import com.driver.models.Card;
import com.driver.models.Student;
import com.driver.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {


    @Autowired
    CardService cardService4;

    @Autowired
    StudentRepository studentRepository4;

    public String getDetailsByEmail(String emailId){
        Student student = studentRepository4.getStudentByEmailId(emailId);


        return student.getName();
    }

    public Student getDetailsById(int id){
        Student student = studentRepository4.findById(id).get();

        return student;
    }

    public void createStudent(Student student){
       Card card =cardService4.createAndReturn(student);
       student.setCard(card);

       studentRepository4.save(student);
    }

    public void updateStudent(Student student){
      //Student student1=studentRepository4.findById(student.getId()).get();
      studentRepository4.save(student);
    }

    public void deleteStudent(int id){
        //Delete student and deactivate corresponding card
        studentRepository4.delete(studentRepository4.findById(id).get());
    }
}
