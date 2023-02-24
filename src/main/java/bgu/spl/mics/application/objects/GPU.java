package bgu.spl.mics.application.objects;

import bgu.spl.mics.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Cluster cluster=Cluster.getInstance();
    private GPUService gpuService;
    private Queue<DataBatch> unTrainedDB;
    private int processTime = 0;
    private DataBatch lastDb = null;
    private long timeUsed = 0;
    private TrainModelEvent currentTrainEvent = null;
    private boolean workingOnModel = false;
    private int currenModelSize = -1;
    private int currenModelCount = 0;
    private ArrayList<Model> trainedModels;





    public GPU(Type type, GPUService gpuService){
        this.type = type;
        cluster.RegisterGpu(this);
        unTrainedDB = new LinkedList<>();
        this.gpuService = gpuService;
        trainedModels = new ArrayList<>();
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    /**
     * @return type
     * @pre: none
     * @post: none
     */
    public Type getType(){
        return type;
    }

    /**
     * @return int
     * @pre: none
     * @post: none
     */
    public int getProcessTime(){
        return processTime;
    }

    /**
     * @return TrainModelEvent
     * @pre: none
     * @post: none
     */
    public TrainModelEvent getCurrentTrainEvent(){
        return currentTrainEvent;
    }

    /**
     * @return TrainModeEvent
     * @inv:none
     * @pre: none
     * @post: none
     */
    public TrainModelEvent tickFunc(){
        if (unTrainedDB.isEmpty()){
            unTrainedDB = cluster.getGPUntrainedQueue(this);
        }
        if (processTime==0) {
            if (currenModelCount == currenModelSize) {
                lastDb = null;
                currenModelCount = 0;
                currenModelSize = -1;
                trainedModels.add(currentTrainEvent.getModel());
                return currentTrainEvent;
            }
            else {
                if(!unTrainedDB.isEmpty()) {
                    lastDb = unTrainedDB.remove();
                    calculateProcessTime();
                    currenModelCount++;
                    processTime--;
                    timeUsed ++;

                }
            }
        }
        else {
            if (lastDb != null) {
                timeUsed ++;
                processTime--;
                currenModelCount++;
            }
        }
        return null;
    }


    /**
     * @return none
     * @inv:none
     * @pre: none
     * @post: process time is updated to the correct process time according to the GPU type
     */
    public void calculateProcessTime(){
        if (type == GPU.Type.RTX3090)
            processTime = 1;
        else if (type == GPU.Type.RTX2080)
            processTime = 2;
        else
            processTime = 4;
    }


    public ArrayList<Model> getTrainedModels() {
        return trainedModels;
    }

    /**
     * @return none
     * @inv: none
     * @pre: none
     * @post: the field currentTrainEvent should be updated
     */
    public void setCurrentTrainEvent(TrainModelEvent currentTrainEvent) {
        this.currentTrainEvent = currentTrainEvent;
    }

    /**
     *
     * @return none
     * @inv: none
     * @pre:none
     * @post: the field currentModelSize should be update
     */
    public void setCurrenModelSize(int currenModelSize) {
        this.currenModelSize = currenModelSize;
    }

}
