package org.RaceGuard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeContainer {
    private static List<String> types = new ArrayList<String>(Arrays.asList(
            RaceGuardE.INT.getValue(), RaceGuardE.INT_ARRAY.getValue() , RaceGuardE.INT_ARRAY_2D.getValue(),//int
            RaceGuardE.DOUBLE.getValue(), RaceGuardE.DOUBLE_ARRAY.getValue(), RaceGuardE.DOUBLE_ARRAY_2D.getValue(),//double
            RaceGuardE.STRING.getValue(), RaceGuardE.STRING_ARRAY.getValue(), RaceGuardE.STRING_ARRAY_2D.getValue(),//string
            RaceGuardE.BOOL.getValue(), RaceGuardE.BOOL_ARRAY.getValue(), RaceGuardE.BOOL_ARRAY_2D.getValue(),//bool
            RaceGuardE.VOID.getValue(),//void
            RaceGuardE.ACTOR.getValue(),//Actor
            RaceGuardE.SCRIPT.getValue()//Script
             )
    );

    public static List<String> getTypes() {
        return types;
    }

    public static void addType(String type) {
        types.add(type);
        types.add(type + "[]");
        types.add(type + "[][]");
    }

    public static boolean hasType(String type) {
        return types.contains(type);
    }
}
