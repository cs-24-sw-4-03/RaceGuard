package org.abcd.examples.ParLang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeContainer {
    private static List<String> types = new ArrayList<String>(Arrays.asList(
            parLangE.INT.getValue(), parLangE.INT_ARRAY.getValue() , parLangE.INT_ARRAY_2D.getValue(),//int
            parLangE.DOUBLE.getValue(), parLangE.DOUBLE_ARRAY.getValue(), parLangE.DOUBLE_ARRAY_2D.getValue(),//double
            parLangE.STRING.getValue(), parLangE.STRING_ARRAY.getValue(), parLangE.STRING_ARRAY_2D.getValue(),//string
            parLangE.BOOL.getValue(),parLangE.BOOL_ARRAY.getValue(), parLangE.BOOL_ARRAY_2D.getValue(),//bool
            parLangE.VOID.getValue(),//void
            parLangE.ACTOR.getValue(),//Actor
            parLangE.SCRIPT.getValue()//Script
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
