package output;
 
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

public class Main {
    public static void main(String[] args){
        ActorSystem system = ActorSystem.create("system");
        ActorRef ryan = system.actorOf(Props.create(Soldier.class), "0");
        ActorRef bo = system.actorOf(Props.create(Soldier.class), "1");
        ActorRef gorm = system.actorOf(Props.create(Soldier.class), "2");
        ActorRef  army = {ryan, bo, gorm};
        ActorRef sven = system.actorOf(Props.create(Soldier.class), "3");
        army[2L] = sven;
        army[1L] = army[2L];
    }
}
