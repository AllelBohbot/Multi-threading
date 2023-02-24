package bgu.spl.mics;

public class TickBroadcast implements Broadcast{
    int time;

    public TickBroadcast(int time)
    {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
