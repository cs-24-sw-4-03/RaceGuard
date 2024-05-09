package output;
 
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

public class Main {
    public static void main(String[] args){
        ActorSystem system = ActorSystem.create("system");
        ActorRef actor3 = system.actorOf(Props.create(myActor3.class), "2");
    }
}
