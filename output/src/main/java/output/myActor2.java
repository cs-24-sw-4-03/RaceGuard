package output;
 
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class myActor2 extends UntypedAbstractActor {
    public myActor2() {
    }
    public void onReceive(Object message) {
        unhandled(message);
    }
}
