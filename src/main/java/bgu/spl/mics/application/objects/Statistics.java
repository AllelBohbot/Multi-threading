package bgu.spl.mics.application.objects;

import java.util.ArrayList;

public class Statistics {

    private ArrayList<Model> trainedModels;
    private int totalNumOfprocessedDB;
    private int gpuUse;
    private int cpuUse;
    private static Cluster cluster=Cluster.getInstance();

    public Statistics(){
        gpuUse = cluster.gpuUseTime();
        cpuUse = cluster.cpuUseTime();
        totalNumOfprocessedDB = cluster.getTotalProcessedData();
        trainedModels = cluster.getTrainedModels();


    }

    public ArrayList<Model> getTrainedModels() {
        return trainedModels;
    }

    public int getCpuUse() {
        return cpuUse;
    }

    public int getGpuUse() {
        return gpuUse;
    }

    public int getTotalNumOfprocessedDB() {
        return totalNumOfprocessedDB;
    }
}
