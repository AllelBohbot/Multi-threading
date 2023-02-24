package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Students> Students;
    private ArrayList<String> GPUS;
    private ArrayList<Integer> CPUS;
    private ArrayList<Conferences> Conferences;
    private int TickTime;
    private int Duration;

    public ArrayList<Students> getStudents(){
        return Students;
    }

    public ArrayList<String> getGPUS(){
        return GPUS;
    }

    public ArrayList<Integer> getCPUS(){
        return CPUS;
    }

    public ArrayList<Conferences> getConferences(){
        return Conferences;
    }

    public int getTickTime(){
        return TickTime;
    }

    public int getDuration(){
        return Duration;
    }


    public class Students{
        String name;
        String department;
        Student.Degree status;
        ArrayList<models> models;

        public String getName(){
            return name;
        }

        public String getDepartment(){
            return department;
        }

        public Student.Degree getStatus(){
            return status;
        }

        public ArrayList<models> getModels(){
            return models;
        }
    }


    public class models{

        String name;
        Data.Type type;
        int size;

        public String getName(){
            return name;
        }

        public Data.Type getType(){
            return type;
        }

        public int getSize(){
            return size;
        }
    }

    public class Conferences{
        String name;
        int date;

        public String getName(){
            return name;
        }

        public int getDate(){
            return date;
        }
    }
}
