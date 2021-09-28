// Code for the process acting on broadcast

import java.util.ArrayList;

import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Char;

public class Pbroad extends UntypedAbstractActor{
	
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this); 
	
	static public Props props(String name) {
		return Props.create(Pbroad.class, () -> new Pbroad(name));
	}
	
	private final String name;
	private Members mem;
	private boolean sentecho = false;
	private int echoes = 0;
	private boolean delivered = false;
	
	public Pbroad(String pName){
		this.name = pName;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
	
		if (msg instanceof Members) {
			this.mem = (Members) msg;
			for(int x = 0; x <= this.mem.Nmembers-1; x = x + 1) {
				log.info(this.name+": know member "+Integer.toString(x)); 
				// this.mem.members.get(x).tell("Message to "+this.mem.members.get(x).path().name(),getSelf()); 
			}
		}
		
		if(msg instanceof TestMessage){
			log.info("Test message received, sending Hello to everybody...");
			
			QuorumRequest m = new QuorumRequest("Hello.");
			for(ActorRef member:this.mem.members){
				member.tell(m, getSelf());
			}
		}
		
		if(msg instanceof QuorumRequest){
			QuorumRequest m = (QuorumRequest) msg;
			ActorRef actorRec = getSender();
			log.info("Received " + m.data + ": " + actorRec.path().name() + " -> " + this.getSelf().path().name());
			QuorumResponse qr = new QuorumResponse("Hi!");
			actorRec.tell(qr, getSelf());
		}
		
		if(msg instanceof QuorumResponse){
			QuorumResponse m = (QuorumResponse) msg;
			ActorRef actorRec = getSender();
			log.info("Received " + m.data + ": " + this.getSelf().path().name() + " <- " + actorRec.path().name() + " = " + m.data);
		}
		
		if(msg instanceof Launch){
			log.info("Launch message received, initiating a new BC round...");
			
			Launch m = (Launch)msg;
			for(ActorRef member:this.mem.members){
				member.tell(new BdMessage("SEND",m.data), getSelf());
			}
		}

		if(msg instanceof BdMessage){
			BdMessage received = (BdMessage) msg;
			
			switch(received.data[0]){
				case "SEND":
					if(!this.sentecho){
						for(ActorRef member:this.mem.members){
							log.info("Echoing message: " + received.data[1]);
							member.tell(new BdMessage("ECHO",received.data[1]), getSelf());
						}
						this.sentecho = true;
					}
					break;
				case "ECHO":
				{
					this.echoes++;
					if(!this.sentecho){
						for(ActorRef member:this.mem.members){
							member.tell(new BdMessage("ECHO",received.data[1]), getSelf());
						}
						this.sentecho = true;
					}
					if(this.echoes > (this.mem.Nmembers/2)){
						if(!this.delivered){
							log.info("I delivered message: " + received.data[1]);
							this.delivered = true;
						}
					}
					break;
				}
			}
			
		}
	
	}
}