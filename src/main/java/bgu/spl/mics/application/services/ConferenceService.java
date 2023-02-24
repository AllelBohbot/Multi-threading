package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link //PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private String name;
    private ConfrenceInformation confrenceInformation;

    public ConferenceService(String name, int date) {
        super(name);
        this.confrenceInformation = new ConfrenceInformation(date, name);
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {                       //ok cause date is final and long is on the heap?
            if (tick.getTime() == confrenceInformation.getDate()) {
                triggerPublishConferenceBroadcast();
                terminate();
            }
        });
    }

    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent publishResultEvent) -> {
            confrenceInformation.UpdateStudentPublishes(publishResultEvent.getStudent(), publishResultEvent.getModel());
            publishResultEvent.getStudent().updateStudentPublishedModels(publishResultEvent.getModel());
        });
    }

    public ConfrenceInformation getConfrenceInformation() {
        return confrenceInformation;
    }

    public void triggerPublishConferenceBroadcast()
    {
        PublishConferenceBroadcast broadcastMessage = new PublishConferenceBroadcast(confrenceInformation);
        this.sendBroadcast(broadcastMessage);
    }

    public ConfrenceInformation getConferenceInformation() {
        return confrenceInformation;
    }
}
