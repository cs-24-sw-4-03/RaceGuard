package org.abcd.examples.ParLang.CodeGen;

import org.abcd.examples.ParLang.symbols.SymbolTable;


//vitistor som input til constructor i stedet for symbolTable
//metoder og visitor kan være ikke-statiske, men så skal der en CodeenUtils instance ind i CodeGenVisitor
public class CodeGenUtils {

    static CodeGenVisitor visitor;

    public CodeGenVisitor getVisitor() {
        return visitor;
    }

    public CodeGenUtils(SymbolTable symbolTable){
        visitor=new CodeGenVisitor(symbolTable);
    }

    /**
     * @return the current line with the correct indentation.
     */
    public static String getLine() {
        String line = visitor.sb.toString().indent(visitor.indent* 4);
        visitor.sb.setLength(0); // Resets string builder
        return line;
    }

    public static void resetStringBuilder(){
        visitor.sb.setLength(0);
        visitor.codeOutput.clear();
        visitor.indent = 0;
    }

}
