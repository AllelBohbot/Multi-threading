package bgu.spl.mics.application.objects;


import bgu.spl.mics.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

class GPUTest {
    enum Type {RTX3090, RTX2080, GTX1080}
    GPU gp=new GPU(GPU.Type.RTX3090,new GPUService("GPU1", GPU.Type.RTX3090));


    @Test void tickFunc(){
       gp.tickFunc();
        if(gp.getProcessTime()==0)
            assertTrue(gp.getCurrentTrainEvent()!=null);
        else
            assertTrue(gp.getCurrentTrainEvent()==null);
    }

    @Test void calculateProcessTime(){
        gp.calculateProcessTime();
        if (gp.getType() == GPU.Type.RTX3090)
            assertTrue(gp.getProcessTime()==1);
        else if (gp.getType() == GPU.Type.RTX2080)
            assertTrue(gp.getProcessTime() == 2);
        else
            assertTrue(gp.getProcessTime() == 4);
    }

    @Test void setCurrentTrainEvent(TrainModelEvent currentTrainEvent){
        gp.setCurrentTrainEvent(currentTrainEvent);
        assertTrue(gp.getCurrentTrainEvent()==currentTrainEvent);
    }

    @Test void setCurrenModelSize(int currenModelSize){
        gp.setCurrenModelSize(currenModelSize);
        assertTrue(gp.getCurrentTrainEvent().getModel().size==currenModelSize);
    }
}