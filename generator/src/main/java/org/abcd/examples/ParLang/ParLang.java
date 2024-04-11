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
import org.abcd.examples.ParLang.symbols.SymbolTable;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.List;
import org.abcd.examples.ParLang.AstNodes.AstNode;


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

        ParseTree tree = parser.init(); // begin parsing at init

        ParLangBaseVisitor<AstNode> visitor=new AstVisitor();

        AstNode ast=tree.accept(visitor);

        //print AST
        System.out.println("AST:");
        AstPrintVisitor astPrintVisitor = new AstPrintVisitor();
        astPrintVisitor.visit(0, ast);

        //print CST
        System.out.println("CST:");
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree

        SymbolTable symbolTable = new SymbolTable();
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor(symbolTable);
        symbolTableVisitor.visit(ast);
    }

}


