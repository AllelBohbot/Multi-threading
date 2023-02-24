package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.Data;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private final long duration;
	//private int time;
	//private final int tickTime;
	private final TimerTask timerTask;
	private bgu.spl.mics.application.objects.Time time;
	private boolean killTimer = false;

	public TimeService(long duration, int tickTime) {
		super("TimeService");
		//this.time = 0;
		this.duration = duration;
		//this.tickTime = tickTime;
		time=new bgu.spl.mics.application.objects.Time(tickTime);
		this.timerTask=new TimerTask() {
			public void run() {
				time.setTime();
				if(time.getTime()==duration) {
					sendBroadcast(new TickBroadcast(-1));
					terminate();
					timerTask.cancel();
				}
				sendBroadcast(new TickBroadcast(time.getTime()));
			}
		};
	}

	// As long as time != duration, the function will send tickBroadcast every tickTime and update the time
	protected void initialize() {
		Timer timer=new Timer();
		timer.scheduleAtFixedRate(timerTask,0,time.getTickTime());
	}



}
