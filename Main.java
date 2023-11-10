
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {

    public static void main(String[] args) {
        // for each input file given to us
        for (int i = 0; i < args.length; i++) {
            // set up streams
            JavaLexer lexer = null;
            try {
                lexer = new JavaLexer(CharStreams.fromFileName(args[i]));
                Warnings.setFileName(args[i]);
            } catch (Exception e) {
                System.out.println("Could not open '" + args[i] + "' for reading.");
                return;
            }
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JavaParser parser = new JavaParser(tokens);
            
            // do the parsing
            ParseTree tree = parser.compilationUnit();

            // we make a list of all the checks we have
            JavaParserBaseVisitor checkers [] = {
                new SwitchCheckVisitor(),
                new StringEqualsVisitor(),
                new IntDivideVisitor(),
                new VoidConstructorVisitor(),
                new ShadowCheckVisitor(),
                new SelfSetVisitor()
            };

            // run all the checkers
            for (JavaParserBaseVisitor checker : checkers) {
                checker.visit(tree);
            }
        }
    }
}

