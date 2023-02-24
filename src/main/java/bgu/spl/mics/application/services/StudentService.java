package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    Student student;
    Future futureService;
    Model currentWorkingModel = null;
    int modelsCounter = 0;

    public StudentService(String name, String department, Student.Degree status, int publications, int papersRead, ArrayList<Model> models) {
        super(name);
        student = new Student(name, department, status, publications, papersRead, models);
        futureService =null;
        this.subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)-> {
            if(tick.getTime()==-1)
                terminate();
            modelManager();
        });
    }

    protected void initialize() {
        this.subscribeBroadcast(PublishConferenceBroadcast.class,(PublishConferenceBroadcast publishConferenceBroadcast)->{
            student.setPapersRead(publishConferenceBroadcast.getConferenceInformation().getStudentPaperRead(student)); //Update the student paper read
        });
    }

    public Student getStudent() {
        return student;
    }

    private void modelManager(){
        if (futureService != null) {
            if (futureService.isDone()) {
                if (futureService.get() == Model.Status.Trained) {
                    futureService = sendEvent(new TestModelEvent(currentWorkingModel, student));
                }
                else {
                    // Checks the model testing result, if good sending to publish
                    if (futureService.get() == Model.Result.Good) {
                        sendEvent(new PublishResultsEvent(currentWorkingModel, student));
                    }
                    futureService = null;
                    currentWorkingModel = null;
                    modelsCounter++;
                }
            }
        }
        // If future is null, send new model for training
        else{
            if (modelsCounter < student.getModels().size()) {
                currentWorkingModel = student.getModels().get(modelsCounter);
                futureService = sendEvent(new TrainModelEvent(currentWorkingModel));
            }
        }
    }
}

