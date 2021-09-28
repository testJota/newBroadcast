// Broadcast Algorithm using Akka toolset

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.Date;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Random;

public class BroadcastSimplified{
	
	public static void main(String[] args){
		
		final int N = 5; 
		//final int f = 2;
		//final double tL = 2;
		//final boolean leader = true;
		final ActorSystem system = ActorSystem.create("system");

		Date now = new Date();

		final ArrayList<ActorRef> members = new ArrayList<ActorRef>();
	
		// System execution time
		SimpleDateFormat dateFormatter = new SimpleDateFormat("E m/d/y h:m:s.SSS z");
		System.out.println("System birth: "+ dateFormatter.format(now));
			
		// Creating the Actors
		for(int x = 0; x <= N-1; x = x + 1) {
			members.add(x,
						system.actorOf(Pbroad.props("P"+Integer.toString(x)),"P"+Integer.toString(x)));
		} 
		
		// Sending the participants to every process
		for(ActorRef member:members){
			member.tell(new Members(members), ActorRef.noSender()); 
		}
		
		// Waiting for everybody to receive members
		try{
			waitBeforeTerminate();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		
		/*
		// System test messages
		TestMessage m = new TestMessage();
		
		for(ActorRef member:members){
			member.tell(m, ActorRef.noSender()); 
		}
		*/
		
		// Initiating a Broadcast round with a random source
		Collections.shuffle(members, new Random());
		Launch launch = new Launch("Success!");
		members.get(0).tell(launch, ActorRef.noSender());
		
		// Waiting for the system termination
		try{
			waitBeforeTerminate();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		
		
		try{
			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		} catch(IOException ioe) {
		} finally {
			system.terminate();
		}
	
	}
	
	public static void waitBeforeTerminate() throws InterruptedException {
		Thread.sleep(2000);
	}
	
}