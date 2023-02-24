package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.*;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final int date;
    //private int publishesCount = 0;
    private HashMap<Student, LinkedList<Model>> studentPublishes;
    private ConferenceService conferenceService;
    private ArrayList<String> publications;
    private String name;

    public ConfrenceInformation(int date, String name){
        this.date = date;
        //conferenceService = new ConferenceService(name, date, this);
        studentPublishes = new HashMap<>();
        publications = new ArrayList<>();
        this.name = name;
    }



    public void UpdateStudentPublishes(Student student, Model model){
        if(studentPublishes.containsKey(student))
            studentPublishes.get(student).add(model);
        else {
            studentPublishes.put(student, new LinkedList<>());
            studentPublishes.get(student).add(model);
        }
    }

    public int getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    //Calculate the number of publishes in the conference that are not belong to the student
    public int getStudentPaperRead(Student student){
        int count = 0;
        for (Student hashMapStudent: studentPublishes.keySet()) {
            if (hashMapStudent != student)
                count += studentPublishes.get(hashMapStudent).size();
        }
        return count;
    }

    public LinkedList<String> getConferencePublishedModels(){
        LinkedList<String> conferencePublishedModels = new LinkedList<>();
        for (Student hashMapStudent: studentPublishes.keySet()) {
            for (int i = 0; i < studentPublishes.get(hashMapStudent).size(); i++)
                conferencePublishedModels.add(studentPublishes.get(hashMapStudent).get(i).getName());
        }
        return conferencePublishedModels;
    }

    public ArrayList<String> getPublications() {
        for (Map.Entry<Student, LinkedList<Model>> entry: studentPublishes.entrySet())
        {
            for (Model model: entry.getValue())
                if (!publications.contains(model.getName()))
                    publications.add(model.getName());
        }
        return publications;
    }
}
