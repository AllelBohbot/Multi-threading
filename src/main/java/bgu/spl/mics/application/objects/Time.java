package bgu.spl.mics.application.objects;

public class Time {

    private int time=0;
    private final int tickTime;

    public Time(int tickTime){
        this.tickTime=tickTime;
    }

    public int getTime() {
        return time;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTime() {
        this.time =this.time+1;
    }
}
