package output;
 
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class myActor1 extends UntypedAbstractActor {
    private long primitive;
    private long arithExp;
    private boolean boolExp;
    private ActorRef actorAccess;
    private ActorRef SELF;
    private long identifier;
    private String String;
    public myActor1(long primitive,long arithExp,boolean boolExp,ActorRef actorAccess,ActorRef SELF,long identifier,String String) {
        this.primitive = primitive;
        this.arithExp = arithExp;
        this.boolExp = boolExp;
        this.actorAccess = actorAccess;
        this.SELF = SELF;
        this.identifier = identifier;
        this.String = String;
    }
    //on method: myTest
    public static final class MyTest {
        public final long primitive;
        public final long arithExp;
        public final boolean boolExp;
        public final ActorRef actorAccess;
        public final ActorRef SELF;
        public final long identifier;
        public final String String;
        public MyTest(long primitive, long arithExp, boolean boolExp, ActorRef actorAccess, ActorRef SELF, long identifier, String String) {
            this.primitive = primitive;
            this.arithExp = arithExp;
            this.boolExp = boolExp;
            this.actorAccess = actorAccess;
            this.SELF = SELF;
            this.identifier = identifier;
            this.String = String;
        }
    }
    private void onMyTest(long primitive,long arithExp,boolean boolExp,ActorRef actorAccess,ActorRef SELF,long identifier,String String) {
        this.primitive = primitive;
        this.arithExp = arithExp;
        this.boolExp = boolExp;
        this.actorAccess = actorAccess;
        this.SELF = SELF;
        this.identifier = identifier;
        this.String = String;
        System.out.println("HEJ MED DIG");
    }
    public void onReceive(Object message) {
        if (message instanceof MyTest myTestMsg) {
            onMyTest(myTestMsg.primitive, myTestMsg.arithExp, myTestMsg.boolExp, myTestMsg.actorAccess, myTestMsg.SELF, myTestMsg.identifier, myTestMsg.String);
        } else {
            unhandled(message);
        }
    }
}
