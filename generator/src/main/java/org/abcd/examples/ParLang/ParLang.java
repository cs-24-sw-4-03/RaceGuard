/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/

package org.abcd.examples.ParLang;
// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.List;

public class ParLang {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(System.in);

        // create a lexer that feeds off of input CharStream
        ParLangLexer lexer = new ParLangLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        ParLangParser parser = new ParLangParser(tokens);

        ParseTree tree = parser.init(); // begin parsing at init rule
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }

    public static boolean parseTest(String realInput){
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(realInput);

        // create a lexer that feeds off of input CharStream
        ParLangLexer lexer = new ParLangLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        ParLangParser parser = new ParLangParser(tokens);

        parser.removeErrorListeners();
        ErrorListener errListen = new ErrorListener();
        parser.addErrorListener(errListen);

        ParseTree tree = parser.init(); // begin parsing at init rule

        List<SyntaxError> errors = errListen.getSyntaxErrors();
        if (errors.size() != 0){return false;}
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
        return true;
    }
    public static List<org.antlr.v4.runtime.Token> lexerTest(String realInput){
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(realInput);

        // create a lexer that feeds off of input CharStream
        ParLangLexer lexer = new ParLangLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        //filling in the tokens
        tokens.fill();

        //create list of tokens to return
        List<org.antlr.v4.runtime.Token> tokenList = tokens.getTokens();
        return tokenList;
    }
}



class ErrorListener extends BaseErrorListener{
    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    public ErrorListener() {
    }

    List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
    }

    @Override
    public String toString() {
        return Utils.join(syntaxErrors.iterator(), "\n");
    }
}

class SyntaxError
{
    private final Recognizer<?, ?> recognizer;
    private final Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String message;
    private final RecognitionException e;

    SyntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
    {
        this.recognizer = recognizer;
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.message = msg;
        this.e = e;
    }

    public Recognizer<?, ?> getRecognizer()
    {
        return recognizer;
    }

    public Object getOffendingSymbol()
    {
        return offendingSymbol;
    }

    public int getLine()
    {
        return line;
    }

    public int getCharPositionInLine()
    {
        return charPositionInLine;
    }

    public String getMessage()
    {
        return message;
    }

    public RecognitionException getException()
    {
        return e;
    }
}