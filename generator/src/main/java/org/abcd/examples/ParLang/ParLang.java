/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/

package org.abcd.examples.ParLang;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.abcd.examples.ParLang.AstNodes.AstNode;

public class ParLang {
    /***
     *
     * @param args - the file path to the source code. Default is '/input/'
     * @throws Exception
     */
    private static final String DEFAULT_INPUT_PATH = System.getProperty("user.dir") + "/input/";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument with file path.");
        }
        Path source = Paths.get(DEFAULT_INPUT_PATH, args[0]);
        validateSource(source);

        CharStream input = CharStreams.fromPath(source); // Load the input file
        ParLangLexer lexer = new ParLangLexer(input); // Create a lexer that feeds off of input CharStream
        CommonTokenStream tokens = new CommonTokenStream(lexer);  // Create a buffer of tokens pulled from the lexer
        ParLangParser parser = new ParLangParser(tokens);   // Create a parser that feeds off the tokens buffer
        ParseTree tree = parser.init(); // Begin parsing at init node

        TypeContainer typeContainer = new TypeContainer();
        ParLangBaseVisitor<AstNode> visitor = new AstVisitor(typeContainer);
        AstNode ast = visitor.visit(tree);

        //printAST(ast, args);
        //printCST(tree, parser);
        generateCode(ast);

    }
    private static void validateSource(Path source) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("File source not found at path: " + source);
        }
        String extension = getFileExtension(source);
        if (!"par".equals(extension)) {
            throw new IOException("Wrong file extension, expected .par");
        }
    }

    /**
     * Get the file extension of a path
     * @param path
     * @return the file extension or an empty string if no extension
     */
    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }

    private static void printAST(AstNode ast, String[] args) {
        System.out.println("AST:");
        AstPrintVisitor astPrintVisitor = new AstPrintVisitor();
        String printOption = args.length > 0 ? args[0] : "";
        astPrintVisitor.visit(0, ast, printOption); // Optionally print parent node info
    }

    private static void printCST(ParseTree tree, ParLangParser parser) {
        System.out.println("CST:");
        System.out.println(tree.toStringTree(parser)); // Print LISP-style tree
    }

    private static void generateCode(AstNode ast) throws IOException {
        CodeGenVisitor codeGenVisitor = new CodeGenVisitor();
        codeGenVisitor.visit(ast);
        codeGenVisitor.generate();
    }

}
