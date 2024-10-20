import com.amoalla.lox.*;
import com.amoalla.lox.Scanner;

int USAGE_EXIT_CODE = 64;
int INCORRECT_INPUT_EXIT_CODE = 65;

ErrorReporter errorReporter = new ErrorReporter();

void main(String[] args) throws IOException {
    if (args.length > 1) {
        System.out.println("Usage: jlox [script]");
        System.exit(USAGE_EXIT_CODE);
    } else if (args.length == 1) {
        runFile(args[0]);
    } else {
        runPrompt();
    }
}

void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    if (errorReporter.hadError) System.exit(INCORRECT_INPUT_EXIT_CODE);
}

void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
        System.out.print("> ");
        String line = reader.readLine();
        if (line == null) break;
        run(line);
        errorReporter.hadError = false;
    }
}

void run(String source) {
    Scanner scanner = new Scanner(source, errorReporter);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens, errorReporter);
    Expr expression = parser.parse();

    // Stop if there was a syntax error.
    if (errorReporter.hadError) return;

    System.out.println(new AstPrinter().print(expression));
}
