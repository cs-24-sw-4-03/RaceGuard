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
import org.abcd.examples.ParLang.AST.AstNode;

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

        ParLangBaseVisitor<AstNode> visitor=new AstVisitor<>();

        //alternative but unsure how accept method works here:
        AstNode ast=tree.accept(visitor);

        AstPrintVisitor astPrintVisitor = new AstPrintVisitor();
        astPrintVisitor.visit(0, ast);


        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}
