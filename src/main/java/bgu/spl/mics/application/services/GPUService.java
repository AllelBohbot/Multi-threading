package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.*;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

//    private Model model;
//    private String name;
//    private long tickTime;
//    private long time;
    private static Cluster cluster=Cluster.getInstance();
    private GPU gpu;
    private Queue<TrainModelEvent> trainModelEventsQueue;

    public GPUService(String name, GPU.Type type) {
        super(name);
        this.gpu = gpu;
        this.gpu = new GPU(type, this);
        trainModelEventsQueue = new LinkedList<>();
        this.subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)->{
            if(tick.getTime()==-1)
                terminate();
            Event event=gpu.tickFunc();
            if(event!=null) {
                complete(event, Model.Status.Trained);
                gpu.setCurrentTrainEvent(null);
            }
            if(gpu.getCurrentTrainEvent() == null){
                if(!trainModelEventsQueue.isEmpty()) {
                    TrainModelEvent tempTrainModelEvent = trainModelEventsQueue.remove();
                    splitAndSend(tempTrainModelEvent.getModel().getData());
                    gpu.setCurrentTrainEvent(tempTrainModelEvent);
                    gpu.setCurrenModelSize(tempTrainModelEvent.getModel().getSize() / 1000);
                }
            }
        });
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(TestModelEvent.class,(TestModelEvent testModelEvent)->{
            resultTestModel(testModelEvent);
        });
        this.subscribeEvent(TrainModelEvent.class,(TrainModelEvent trainModelEvent)->{
            if (gpu.getCurrentTrainEvent() != null) {
                splitAndSend(trainModelEvent.getModel().getData());
                gpu.setCurrentTrainEvent(trainModelEvent);
                gpu.setCurrenModelSize(trainModelEvent.getModel().getSize() / 1000);
            }
            else
                trainModelEventsQueue.add(trainModelEvent);
        });
    }

    public GPU getGpu() {
        return gpu;
    }

//    public long getTimeUsed() {
//        return timeUsed;
//    }

    //Get data model, split it to dataBatches and send it to the cluster queue
    public void splitAndSend(Data data){
        Queue<DataBatch> dbQueue = new LinkedList<>();
        int count = 0;
        DataBatch dataBatch;
        while (count< data.getSize()) {
            dataBatch = new DataBatch(data, count, this.getGpu());
            dbQueue.add(dataBatch);
            count+=1000;
        }
        cluster.GpuServiceUpdate(dbQueue);
    }

    private void resultTestModel(TestModelEvent testModelEvent) {
        Random rand = new Random();
        if (testModelEvent.getStudent().getStatus() == Student.Degree.PhD) {
            if (rand.nextInt(99) > 19)
                complete(testModelEvent, Model.Result.Good);
            else
                complete(testModelEvent, Model.Result.Bad);

        } else {
            if (rand.nextInt(99) > 39)
                complete(testModelEvent, Model.Result.Good);
            else
                complete(testModelEvent, Model.Result.Bad);
        }
        testModelEvent.getModel().setStatus(Model.Status.Tested);
    }


}
