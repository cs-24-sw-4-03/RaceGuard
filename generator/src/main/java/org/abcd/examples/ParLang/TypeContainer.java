package org.abcd.examples.ParLang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeContainer {
    private static List<String> types = new ArrayList<String>(Arrays.asList(
            "int", "int[]", "int[][]", "double",  "double[]", "double[][]", "string", "string[]", "string[][]", "bool", "bool[]", "bool[][]", "void", "Actor", "Script"));;

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
