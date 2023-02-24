package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CPU service is responsible for handling the {//@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private Queue<DataBatch> dataBatchQueue;
    //private long time = 0;
    private static Cluster cluster=Cluster.getInstance();
    private CPU cpu;
    private DataBatch currentDataBatch=null;
    private long timeUsed;
    private int totalProcessedData = 0;
    //private int duration;

    public CPUService(String name, int cores) {
        super(name);
        cpu = new CPU(cores, this);
        dataBatchQueue=new LinkedList<>();
        this.cpu = cpu;
        this.subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)->{
            if(tick.getTime()==-1)
                terminate();
            cpu.tickFunc();
        });
    }



    @Override
    protected void initialize() {
    }

    public CPU getCpu() {
        return cpu;
    }


    public long getTimeUsed() {
        return timeUsed;
    }

    public int getTotalProcessedData() {
        return totalProcessedData;
    }


}
