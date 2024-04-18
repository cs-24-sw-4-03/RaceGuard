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
import org.abcd.examples.ParLang.AstNodes.InitNode;
import org.abcd.examples.ParLang.symbols.SymbolTable;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.abcd.examples.ParLang.AstNodes.AstNode;


public class ParLang {
    /***
     *
     * @param args running main with "parents" as args[0] tells AstPrintVisitor to print AstNodes with information about parent fields.
     * @throws Exception
     */
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

        TypeContainer typeContainer = new TypeContainer();
        ParLangBaseVisitor<AstNode> visitor=new AstVisitor(typeContainer);

        InitNode ast=(InitNode) tree.accept(visitor);

        //print AST
        System.out.println("AST:");
        AstPrintVisitor astPrintVisitor = new AstPrintVisitor();
        String printOption="";
        if(args.length>0){
            printOption=args[0];
        }
        astPrintVisitor.visit(0, ast, printOption); //if printOption is "parents" then tree is printed with info about parent node of each AstNode.

        //print CST
        System.out.println("CST:");
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree

        System.out.println("Scoping");
        SymbolTable symbolTable = new SymbolTable();
        FuncVisitor funcVisitor = new FuncVisitor(symbolTable);
        funcVisitor.visit(ast);
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor(symbolTable);
        symbolTableVisitor.visit(ast);
        
        System.out.println("Type Checking");
        TypeVisitor typeVisitor = new TypeVisitor(symbolTable, typeContainer);
        typeVisitor.visit(ast);
    }

}


