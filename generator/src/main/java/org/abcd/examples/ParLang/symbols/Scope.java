package org.abcd.examples.ParLang.symbols;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    private String scopeName;
    private Scope parent;

    //The symbols in the scope
    private HashMap<String, Attributes> symbols = new HashMap<>();
    private Map<String, Attributes> params = new LinkedHashMap<>();

}
