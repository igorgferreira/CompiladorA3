// Arquivo: src/main/java/claude/teste/AnalisadorSintatico.java
package compilador.teste;

import java.util.HashSet;
import java.util.Set;

public class AnalisadorSintatico {
    private AnalisadorLexico aLexico;
    private Emissor emissor;
    private Set<String> symbols;
    private Set<String> labelsDeclared;
    private Set<String> labelsGotoed;
    private Token curToken;
    private Token peekToken;

    public AnalisadorSintatico(AnalisadorLexico AnalisadorLexico, Emissor emissor) {
        this.aLexico = AnalisadorLexico;
        this.emissor = emissor;
        this.symbols = new HashSet<>();
        this.labelsDeclared = new HashSet<>();
        this.labelsGotoed = new HashSet<>();
        this.curToken = null;
        this.peekToken = null;

        this.nextToken();
        this.nextToken();
    }

    // Métodos auxiliares permanecem iguais
    public boolean checkToken(TipoToken kind) { return kind == this.curToken.kind; }
    public boolean checkPeek(TipoToken kind) { return kind == this.peekToken.kind; }
    public void nextToken() {
        this.curToken = this.peekToken;
        this.peekToken = this.aLexico.obterToken();
    }
    public void match(TipoToken kind) {
        if (!this.checkToken(kind)) this.abort("Esperado " + kind.name() + ", obtido " + this.curToken.kind.name());
        this.nextToken();
    }
    public void abort(String message) {
        System.err.println("Erro Sintático: " + message);
        System.exit(1);
    }
    public boolean isComparisonOperator() {
        return this.checkToken(TipoToken.GT) || this.checkToken(TipoToken.GTEQ) ||
                this.checkToken(TipoToken.LT) || this.checkToken(TipoToken.LTEQ) ||
                this.checkToken(TipoToken.EQEQ) || this.checkToken(TipoToken.NOTEQ);
    }

    // --- REGRAS DE PRODUÇÃO (GERAÇÃO JAVA) ---

    public void program() {
        // Configura o cabeçalho da classe Java
        // Importante: O nome da classe deve bater com o nome do arquivo (ProgramaGerado)
        this.emissor.headerLine("import java.util.Scanner;");
        this.emissor.headerLine("public class ProgramaGerado {");
        this.emissor.headerLine("    public static void main(String[] args) {");
        this.emissor.headerLine("        Scanner scanner = new Scanner(System.in);");

        while (this.checkToken(TipoToken.NEWLINE)) {
            this.nextToken();
        }

        while (!this.checkToken(TipoToken.EOF)) {
            this.statement();
        }

        // Fecha o main e a classe
        this.emissor.emitLine("        scanner.close();");
        this.emissor.emitLine("    }");
        this.emissor.emitLine("}");

        // Verificação de GOTO (Aviso: Java não suporta goto nativo)
        for (String label : this.labelsGotoed) {
            if (!this.labelsDeclared.contains(label)) {
                this.abort("Tentando ir para um rótulo não declarado: " + label);
            }
        }
    }

    public void statement() {
        // PRINT -> System.out.println
        if (this.checkToken(TipoToken.PRINT)) {
            this.nextToken();
            if (this.checkToken(TipoToken.STRING)) {
                this.emissor.emitLine("        System.out.println(\"" + this.curToken.text + "\");");
                this.nextToken();
            } else {
                String expr = this.expression();
                this.emissor.emitLine("        System.out.println(" + expr + ");");
            }
        }
        // IF
        else if (this.checkToken(TipoToken.IF)) {
            this.nextToken();
            String comp = this.comparison();
            this.match(TipoToken.THEN);
            this.nl();
            this.emissor.emitLine("        if(" + comp + ") {");

            while (!this.checkToken(TipoToken.ENDIF)) {
                this.statement();
            }
            this.match(TipoToken.ENDIF);
            this.emissor.emitLine("        }");
        }
        // WHILE
        else if (this.checkToken(TipoToken.WHILE)) {
            this.nextToken();
            String comp = this.comparison();
            this.match(TipoToken.REPEAT);
            this.nl();
            this.emissor.emitLine("        while(" + comp + ") {");

            while (!this.checkToken(TipoToken.ENDWHILE)) {
                this.statement();
            }
            this.match(TipoToken.ENDWHILE);
            this.emissor.emitLine("        }");
        }
        // LABEL (Java tem labels, mas uso é restrito)
        else if (this.checkToken(TipoToken.LABEL)) {
            this.nextToken();
            if (this.labelsDeclared.contains(this.curToken.text)) {
                this.abort("O rótulo já existe: " + this.curToken.text);
            }
            this.labelsDeclared.add(this.curToken.text);
            this.emissor.emitLine("        " + this.curToken.text + ":;"); // Label Java
            this.match(TipoToken.IDENT);
        }
        // GOTO (Não suportado nativamente em Java, geramos um comentário ou erro)
        else if (this.checkToken(TipoToken.GOTO)) {
            this.nextToken();
            this.labelsGotoed.add(this.curToken.text);
            this.emissor.emitLine("        // GOTO " + this.curToken.text + " (Aviso: Java não suporta GOTO não estruturado);");
            this.match(TipoToken.IDENT);
        }
        // LET -> Atribuição de variáveis
        else if (this.checkToken(TipoToken.LET)) {
            this.nextToken();
            String varName = this.curToken.text;

            // Se a variável não existe, declara no cabeçalho como double
            if (!this.symbols.contains(varName)) {
                this.symbols.add(varName);
                this.emissor.headerLine("        int " + varName + ";");
            }

            this.match(TipoToken.IDENT);
            this.match(TipoToken.EQ);

            String expr = this.expression();
            this.emissor.emitLine("        " + varName + " = " + expr + ";");
        }
        // INPUT -> scanner.nextDouble()
        else if (this.checkToken(TipoToken.INPUT)) {
            this.nextToken();
            String varName = this.curToken.text;

            if (!this.symbols.contains(varName)) {
                this.symbols.add(varName);
                this.emissor.headerLine("        double " + varName + ";");
            }

            this.emissor.emitLine("        " + varName + " = scanner.nextDouble();");
            this.match(TipoToken.IDENT);
        }
        else {
            this.abort("Declaração inválida: " + this.curToken.text);
        }
        this.nl();
    }

    // Métodos de Expressão retornam String para compor o código
    public String comparison() {
        String res = this.expression();
        if (this.isComparisonOperator()) {
            res += " " + this.curToken.text + " ";
            this.nextToken();
            res += this.expression();
        } else {
            this.abort("Operador de comparação esperado.");
        }
        while (this.isComparisonOperator()) {
            res += " " + this.curToken.text + " ";
            this.nextToken();
            res += this.expression();
        }
        return res;
    }

    public String expression() {
        String res = this.term();
        while (this.checkToken(TipoToken.PLUS) || this.checkToken(TipoToken.MINUS)) {
            res += " " + this.curToken.text + " ";
            this.nextToken();
            res += this.term();
        }
        return res;
    }

    public String term() {
        String res = this.unary();
        while (this.checkToken(TipoToken.ASTERISK) || this.checkToken(TipoToken.SLASH)) {
            res += " " + this.curToken.text + " ";
            this.nextToken();
            res += this.unary();
        }
        return res;
    }

    public String unary() {
        String res = "";
        if (this.checkToken(TipoToken.PLUS) || this.checkToken(TipoToken.MINUS)) {
            res += this.curToken.text;
            this.nextToken();
        }
        res += this.primary();
        return res;
    }

    public String primary() {
        String res = "";
        if (this.checkToken(TipoToken.NUMBER)) {
            res = this.curToken.text;
            this.nextToken();
        } else if (this.checkToken(TipoToken.IDENT)) {
            if (!this.symbols.contains(this.curToken.text)) {
                this.abort("Variável não declarada: " + this.curToken.text);
            }
            res = this.curToken.text;
            this.nextToken();
        } else {
            this.abort("Token inesperado: " + this.curToken.text);
        }
        return res;
    }

    public void nl() {
        this.match(TipoToken.NEWLINE);
        while (this.checkToken(TipoToken.NEWLINE)) {
            this.nextToken();
        }
    }
}