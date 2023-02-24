package bgu.spl.mics.application.objects;


import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.TimeService;
import org.junit.Test;
import static org.junit.Assert.*;

class CPUTest {

    CPU cp=new CPU(32,new CPUService("cpu1",32));

    @Test void sendDBtoService(DataBatch dataBatch){
        sendDBtoService(dataBatch);
        if(dataBatch==null)
            assertTrue(cp.getCurrentDataBatch()==dataBatch);
        else {
                int cpuFactor = (32 / cp.getCores());
                if (dataBatch.getData().getType() == Data.Type.Images)
                    assertTrue(cp.getProcessTime() == cpuFactor * 4);
                else if (dataBatch.getData().getType() == Data.Type.Text)
                    assertTrue(cp.getProcessTime() == cpuFactor * 2);
                else
                    assertTrue(cp.getProcessTime() == cpuFactor);
        }
    }

    @Test void tickFunc(){
        int timeUsed=cp.getTimeUsed();
        int processTime=cp.getProcessTime();
        cp.tickFunc();
        if(processTime!=0){
            assertTrue(timeUsed+1==cp.getTimeUsed());
            assertTrue(processTime-1==cp.getProcessTime());
        }
    }
}