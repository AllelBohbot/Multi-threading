package bgu.spl.mics;

import bgu.spl.mics.application.objects.ConfrenceInformation;

public class PublishConferenceBroadcast implements Broadcast{
    ConfrenceInformation confrenceInformation;

    public PublishConferenceBroadcast(ConfrenceInformation confrenceInformation){
        this.confrenceInformation = confrenceInformation;
    }

    public ConfrenceInformation getConferenceInformation() {
        return confrenceInformation;
    }
}
