import java.util.ArrayList;

public class ArrayListTest {

    private static class A{

    }

    public static void main(String[] args){
        ArrayList<A> actorList=new ArrayList<A>();
        actorList.add(new A());
        actorList.add(new A());

        ArrayList<Integer> intList=new ArrayList<Integer>();

        intList.add(2);
        intList.add(3);

        System.out.println(actorList);
        System.out.println(intList);
    }
}
