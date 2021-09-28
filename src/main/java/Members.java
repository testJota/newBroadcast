// Members class

import akka.actor.ActorRef;
import java.util.ArrayList;

public class Members{
	public final int Nmembers;
	public final ArrayList<ActorRef> members; 

	public Members(ArrayList<ActorRef> members){
		this.Nmembers = members.size();
		this.members = members;
	}
}