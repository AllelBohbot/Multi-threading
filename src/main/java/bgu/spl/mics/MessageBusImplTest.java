package bgu.spl.mics;

import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Test;
import static org.junit.Assert.*;

class MessageBusImplTest {

    MessageBusImpl msb = MessageBusImpl.getInstance();
    MicroService microService = new GPUService("mTest", GPU.Type.RTX3090);
    ExampleEvent event = new ExampleEvent("eTest");
    ExampleBroadcast broadcast = new ExampleBroadcast("bTest");
    Future<String> future = new Future<>();
    String eventResult;
    Message message = null;

    @Test void getInstance() {
        msb = msb.getInstance();
        if (msb == null) {
            assertTrue(msb != null);
        } else {
            assertTrue(msb != null);
        }
    }

    @Test void testSubscribeEvent() {
        msb.subscribeEvent(event.getClass(), microService);
        assertTrue(msb.isSubscribed(event, microService));
    }

    @Test void testSubscribeBroadcast() {
        msb.subscribeBroadcast(broadcast.getClass(), microService);
        assertTrue(msb.isSubscribed(broadcast, microService));
    }

    @Test void complete() {
        future = msb.sendEvent(event);
        msb.complete(event, eventResult);
        assertTrue(future.get() == eventResult);
    }

    @Test void sendBroadcast() {
        msb.sendBroadcast(broadcast);
        assertTrue(msb.wasSent(broadcast));
    }

    @Test void sendEvent() {
        msb.sendEvent(event);
        assertTrue(msb.wasSent(event));
    }

    @Test void register() {
        msb.register(microService);
        assertTrue(msb.isRegistered(microService));
    }

    @Test void unregister() {
        msb.register(microService);
        assertFalse(msb.isRegistered(microService));
    }

    @Test void testAwaitMessage() throws InterruptedException{
        message = msb.awaitMessage(microService);
        assertTrue(message != null);
    }
}