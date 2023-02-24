package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.services.CPUService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private HashMap<GPU, Queue<DataBatch>> gpuUntrainedDb;
	private Queue<DataBatch> unProcessedDb;
	private Queue<CPU> availableCpus;
	private LinkedList<CPU> unAvailableCpus;
	private static Cluster clusterInstance = null;
	private ArrayList<CPU> allCPUs;
	private ArrayList<GPU> allGPUs;
	private ArrayList<Model> trainedModels;


	private Cluster(){
		gpuUntrainedDb=new HashMap<>();
		availableCpus=new LinkedList<>();
		unAvailableCpus=new LinkedList<>();
		unProcessedDb = new LinkedList<>();
		allCPUs = new ArrayList<>();
		allGPUs = new ArrayList<>();
		trainedModels = new ArrayList<>();
	}

	public int cpuUseTime(){
		int timeCount = 0;
		for(CPU c: allCPUs){
			timeCount+= c.getTimeUsed();
		}
		return timeCount;
	}

	public int gpuUseTime(){
		int timeCount = 0;
		for(GPU g: allGPUs){
			timeCount+= g.getTimeUsed();
		}
		return timeCount;
	}

	public int getTotalProcessedData(){
		int processedDataCount = 0;
		for (CPU c: allCPUs){
			processedDataCount += c.getTotalProcessedData();
		}
		return  processedDataCount;
	}

	public ArrayList<Model> getTrainedModels(){
		for(GPU g : allGPUs)
			for (Model m : g.getTrainedModels())
				trainedModels.add(m);
		return trainedModels;
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (clusterInstance == null) {
			clusterInstance = new Cluster();
		}
		return clusterInstance;
	}

	public void RegisterCpu(CPU cpu){
		availableCpus.add(cpu);
		allCPUs.add(cpu);
	}

	public void RegisterGpu(GPU gpu){
		Queue dataBatch=new LinkedList();
		gpuUntrainedDb.put(gpu,dataBatch);
		allGPUs.add(gpu);
	}

	public void CpuUpdate(CPU cpu, DataBatch dataBatch)
	{
		synchronized (unAvailableCpus) {
			unAvailableCpus.remove(cpu);
			unAvailableCpus.notifyAll();
		}
		synchronized (availableCpus) {
			availableCpus.add(cpu);
			availableCpus.notifyAll();
		}
		synchronized (gpuUntrainedDb) {
			if (dataBatch != null) {
				gpuUntrainedDb.get(dataBatch.getGpu()).add(dataBatch);
				gpuUntrainedDb.notifyAll();
			}
		}
		processDB();
	}

	//gpu bring unprocessed data and cluster send to process
	public void GpuServiceUpdate(Queue<DataBatch> dbQueue){
		synchronized (unProcessedDb) {
			while (!dbQueue.isEmpty())
				unProcessedDb.add(dbQueue.remove());
			synchronized (availableCpus) {
				while (!unProcessedDb.isEmpty() && !availableCpus.isEmpty()) {
					processDB();
				}
				availableCpus.notifyAll();
			}
			unProcessedDb.notifyAll();
		}
	}

	public void processDB(){
		synchronized (availableCpus) {
			while (!availableCpus.isEmpty()) {
				CPU cpu = availableCpus.remove();
				synchronized (unProcessedDb) {
					if (!unProcessedDb.isEmpty()) {
						DataBatch dataBatch = unProcessedDb.remove();
						cpu.sendDBtoService(dataBatch);
						synchronized (unAvailableCpus) {
							unAvailableCpus.add(cpu);
							unAvailableCpus.notifyAll();
						}
					}
					else {
						cpu.sendDBtoService(null);
					}
					unProcessedDb.notifyAll();
				}
			}
			availableCpus.notifyAll();
		}
	}

	//Gets a GPU instance and return its corresponding queue of processed DataBatches
	public Queue<DataBatch> getGPUntrainedQueue(GPU gpu) {
		int limit;
		if(gpu.getType()== GPU.Type.RTX3090)
			limit=32;
		else if(gpu.getType()== GPU.Type.RTX2080)
			limit=16;
		else
			limit=8;
		Queue<DataBatch> dataBatchQueue=new LinkedList<>();
		synchronized (gpuUntrainedDb) {
			for (int i = 0; i < limit && !gpuUntrainedDb.get(gpu).isEmpty(); i++) {
				dataBatchQueue.add(gpuUntrainedDb.get(gpu).remove());
			}
			gpuUntrainedDb.notifyAll();
		}
		return dataBatchQueue;
	}

}
