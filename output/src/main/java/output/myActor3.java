package output;
 
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class myActor3 extends UntypedAbstractActor {
    private long varTest = 1L;
    public myActor3() {
        ActorRef actor2 = getContext().actorOf(Props.create(myActor2.class), "0");
        ActorRef actor1 = getContext().actorOf(Props.create(myActor1.class, 1L, 2L + 2L, true, actor2, getSelf(), this.varTest, "Hello"), "1");
        actor1.tell(new myActor1.MyTest(1L,2L + 2L,true,actor2,getSelf(),this.varTest,"Hello"),getSelf());
    }
    public void onReceive(Object message) {
        unhandled(message);
    }
}
