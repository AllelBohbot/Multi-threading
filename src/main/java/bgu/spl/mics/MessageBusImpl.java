package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private HashMap<MicroService, Queue<Message>> MSMessageQueue; // The queues of the microservices. list of pairs while the first element is the microservice's name and the second is its queue
	private static MessageBusImpl messageBusInstance = null;
	private HashMap<Class<? extends Message>,LinkedList<MicroService>> MSSubscribedMessage; //list of MS types and its subscribed messages
	private HashMap<Event,Future> eventToFuture;

	private MessageBusImpl() {
		MSMessageQueue=new HashMap<>();
		MSSubscribedMessage=new HashMap<>();
		eventToFuture=new HashMap<>();
	}


	/**
	 * @return instance of messageBus
	 * @inv:none
	 * @pre: none
	 * @post: the instance of messagebus is returned if hasn't been initialized the initialized and them return
	 */
	// Static method to create instance of Singleton class
	public static MessageBusImpl getInstance() {
		// To ensure only one instance is created
		if (messageBusInstance == null) {
			messageBusInstance = new MessageBusImpl();
		}
		return messageBusInstance;
	}

	/**
	 * @return boolean
	 * @inv:none
	 * @pre: none
	 * @post: return true or false if a micro service is subscribed to a message type
	 */
	//Checks if a microservice is subscribed to a message type. Aid test function
	public boolean isSubscribed(Message message, MicroService m) {
		if(MSSubscribedMessage.containsKey(message.getClass())){
			if(MSSubscribedMessage.get(message.getClass()).contains(m))
				return true;
		}
		return false;
	}

	/**
	 * @return boolean
	 * @inv:none
	 * @pre: none
	 * @post: aid function for the function above
	 */
	//Checks if a microservice is registered to the messageBus.Aid test function
	public boolean isRegistered(MicroService m) {
		return MSMessageQueue.containsKey(m);
	}

	/**
	 * @return boolean
	 * @inv:none
	 * @pre: none
	 * @post: aid function for the function above
	 */
	//Checks if a message was sent.Aid test function
	public boolean wasSent(Message m) {
		for(int i=0;i<MSMessageQueue.size();i++){
			Iterator<Message> it = MSMessageQueue.get(i).iterator();
			while (it.hasNext())
			{
				if(m == it)
					return true;
				it.next();
			}
		}
		return false;
	}


	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: a micro service subscribe in to an event type
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		synchronized (MSSubscribedMessage) {
			if (MSSubscribedMessage.containsKey(type)) {
				MSSubscribedMessage.get(type).add(m);
			} else {
				LinkedList<MicroService> list = new LinkedList<>();
				list.add(m);
				MSSubscribedMessage.put(type, list);
			}
			MSSubscribedMessage.notifyAll();
		}
	}


	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: a micro service subscribe in to a broadcast type
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		synchronized (MSSubscribedMessage) {
			if (MSSubscribedMessage.containsKey(type)) {
				MSSubscribedMessage.get(type).add(m);
			} else {
				LinkedList<MicroService> list = new LinkedList<>();
				list.add(m);
				MSSubscribedMessage.put(type, list);
			}
			MSSubscribedMessage.notifyAll();
		}
	}

	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: future is updated with result
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		eventToFuture.get(e).resolve(result);
	}


	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: a broadcast is sent to every micro service subscribed to it
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		synchronized (MSSubscribedMessage) {
			synchronized (MSMessageQueue) {
				LinkedList<MicroService> msList = MSSubscribedMessage.get(b.getClass());
				for (int i = 0; i < msList.size(); i++) {
					(MSMessageQueue.get(msList.get(i))).add(b);
				}
				MSMessageQueue.notifyAll();
			}
			MSSubscribedMessage.notifyAll();
		}
	}

	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: an event is sent to every micro service subscribed to it
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		try {
			synchronized (MSSubscribedMessage) {
				synchronized (MSMessageQueue) {
					LinkedList<MicroService> msList = MSSubscribedMessage.get(e.getClass());
					MSMessageQueue.get(msList.getFirst()).add(e);
					MicroService temp = msList.removeFirst();
					msList.addLast(temp);
					MSMessageQueue.notifyAll();
				}
				MSSubscribedMessage.notifyAll();
			}
			Future<T> future = new Future<>();
			synchronized (eventToFuture) {
				eventToFuture.put(e, future);
				eventToFuture.notifyAll();
			}
			return future;
		}
		catch (Exception exception){
			return null;
		}
	}

	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: a micro service is registered
	 */
	@Override
	public void register(MicroService m) {
		Queue<Message> msQueue=new LinkedList<>();
		synchronized (MSMessageQueue) {
			MSMessageQueue.put(m, msQueue);
			MSMessageQueue.notifyAll();
		}
	}

	/**
	 * @return none
	 * @inv:none
	 * @pre: none
	 * @post: a micro service is unregistered
	 */
	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
		synchronized (MSMessageQueue) {
			synchronized (MSSubscribedMessage) {
				MSMessageQueue.remove(m);
				for (Class<? extends Message> ms: MSSubscribedMessage.keySet()) {
					if(MSSubscribedMessage.get(ms).contains(m))
						MSSubscribedMessage.get(ms).remove(m);
				}
				MSSubscribedMessage.notifyAll();
			}
			MSMessageQueue.notifyAll();
		}
	}

	/**
	 * @return message
	 * @inv:none
	 * @pre: none
	 * @post: we get a message
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		Message ms;
		synchronized (MSMessageQueue){
			while (MSMessageQueue.get(m).isEmpty()) {
				MSMessageQueue.wait();
			}
			ms=MSMessageQueue.get(m).remove();
			MSMessageQueue.notifyAll();
		}
		return ms;
	}
}
