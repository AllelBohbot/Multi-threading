package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event {

    Model model;
    Student student;

    public TestModelEvent(Model model, Student student){
        this.model=model;
        this.student=student;
    }

    public Model getModel() {
        return model;
    }

    public Student getStudent() {
        return student;
    }
}
