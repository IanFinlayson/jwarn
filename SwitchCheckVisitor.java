import java.util.List;

public class SwitchCheckVisitor extends JavaParserBaseVisitor<Void> {

    // returns true if a break can be found within a (possibly nested) statement
    public boolean checkForBreak(JavaParser.StatementContext stmt) {
        if (stmt == null) {
            // if there is no statement, it could be a declaration or sth else
            return false;
        } else if (stmt.BREAK() != null) {
            // we found a break!
            return true;
        } else if (stmt.block() != null) {
            // it's a nested one, such as arises from { } in the case. recurse
            for (JavaParser.BlockStatementContext block : stmt.block().blockStatement()) {
                if (checkForBreak(block.statement())) {
                
                    return true;
                }
            }
        }
        
        return false;
    }

    // check for a break within a statement group under a switch statement
    public void checkForBreak(JavaParser.SwitchBlockStatementGroupContext ctx) {
		boolean break_found = false;
		for (JavaParser.BlockStatementContext stmt : ctx.blockStatement()) {
            if (checkForBreak(stmt.statement())) {
                break_found = true;
            }
        }

        if (!break_found) {
            System.out.println("Switch case on line " + ctx.getStart().getLine() + " missing break.");
        }
    }


    // hook into the antlr visitor system to catch switch statements
    @Override
    public Void visitStatement(JavaParser.StatementContext stmt) {
        // if it's not a switch statement, just return
        if (stmt.SWITCH() == null) {
            return null;
        }
		
        // get the different case blocks, and go through all but last (which needs no break)
        List<JavaParser.SwitchBlockStatementGroupContext> blocks = stmt.switchBlockStatementGroup();
        for (int i = 0; i < blocks.size() - 1; i++) {
            checkForBreak(blocks.get(i));
        }
        
        // recurse (in order to get switches that may be nested themselves)
        return visitChildren(stmt);
    }
}
