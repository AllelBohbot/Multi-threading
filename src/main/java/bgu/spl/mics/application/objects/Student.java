package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.StudentService;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree { //I changed it to public, can I?
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    ArrayList<Model> models;
    ArrayList<Model> publishedModel;

    public Student(String name, String department, Degree status, int publications, int papersRead, ArrayList<Model> models){
        this.name = name;
        this.department=department;
        this.status=status;
        this.publications=publications;
        this.papersRead=papersRead;
        this.models=models;
        publishedModel =new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    // Return list of trained models
    public ArrayList<Model> getTrainedModels(){
        ArrayList<Model> trainedModels = new ArrayList<>();
        for (int i = 0; i< models.size();i++) {
            if (models.get(i).getStatus() == Model.Status.Trained || models.get(i).getStatus() == Model.Status.Tested)
                trainedModels.add(models.get(i));
        }
        return trainedModels;
    }

    public String getDepartment(){
        return department;
    }

    public Degree getStatus(){
        return status;
    }

    public ArrayList<Model> getPublishedModel(){
        return publishedModel;
    }

    public void updateStudentPublishedModels(Model model){
        publishedModel.add(model);
    }

    public int getPublications(){
        return publications;
    }

    public int getPapersRead(){
        return papersRead;
    }

    public ArrayList<Model> getModels(){
        return models;
    }

    public void setPublications(int pubNum){
        publications=pubNum;
    }

    public void setPapersRead(int papersReadNum){
        papersRead=papersReadNum;
    }

}
