package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class PublishResultsEvent implements Event {
    Student student;
    Model model;

    public PublishResultsEvent(Model model, Student student){
        this.student = student;
        this.model = model;
    }

    public Student getStudent() {
        return student;
    }

    public Model getModel() {
        return model;
    }
}
