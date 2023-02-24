package bgu.spl.mics.application.objects;


import bgu.spl.mics.application.services.CPUService;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private final int cores;
    private static Cluster cluster=Cluster.getInstance();
    private CPUService cpuService;
    private int processTime = 0;
    private DataBatch currentDataBatch = null;
    private int timeUsed = 0;
    private int totalProcessedData = 0;


    public CPU(int cores, CPUService cpuService) {
        this.cores = cores;
        cluster.RegisterCpu(this);
        this.cpuService = cpuService;
    }

    public int getTotalProcessedData() {
        return totalProcessedData;
    }

    /**
     * @pre:none
     * @inv:cores>0
     * @post:none
     * @return cores
     */
    public int getCores(){
        return cores;
    }

    /**
     * @pre:none
     * @inv: none
     * @post:none
     * @return DataBatch
     */
    public DataBatch getCurrentDataBatch(){
        return currentDataBatch;
    }

    /**
     * @pre:none
     * @inv: none
     * @post:none
     * @return int
     */
    public int getProcessTime(){
        return processTime;
    }

    /**
     * @pre:none
     * @inv: none
     * @post:none
     * @return int
     */
    public int getTimeUsed(){
        return timeUsed;
    }

    /**
     * @pre: none
     * @inv: none
     * @post: process time is updated according to the current type of data
     * @return none
     */
    public void sendDBtoService(DataBatch dataBatch) {
        int cpuFactor = (32 / getCores());
        if (dataBatch != null) {
            if (dataBatch.getData().getType() == Data.Type.Images)
                processTime = cpuFactor * 4;
            else if (dataBatch.getData().getType() == Data.Type.Text)
                processTime = cpuFactor * 2;
            else
                processTime = cpuFactor;
        }
        currentDataBatch=dataBatch;
    }

    /**
     * @pre: none
     * @inv: none
     * @post: data has been processed and one tick had past
     * @return none
     */
    public void tickFunc(){
        if(processTime!=0){
            timeUsed++;
            processTime--;
        }
        else {
            if (currentDataBatch != null) {
                DataBatch tempDataBatch = currentDataBatch;
                cluster.CpuUpdate(this, currentDataBatch);
                totalProcessedData++;
                if (currentDataBatch == tempDataBatch)
                    currentDataBatch = null;
            }
        }
    }
}
