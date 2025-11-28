package compilador.teste;

public class AnalisadorLexico {
    private String source;
    private char curChar;
    private int curPos;

    // Método Construtor
    public AnalisadorLexico(String source) {
        this.source = source + '\n'; // Adicione uma nova linha para simplificar a análise léxica do último instrução/token
        this.curChar = '\0';
        this.curPos = -1;
        this.proximoCaractere(); // Verifica o próximo caractere
    }

    // Processa o próximo caractere
    public void proximoCaractere() {
        this.curPos += 1;
        if (this.curPos >= this.source.length()) {
            this.curChar = '\0'; // Fim de Arquivo
        } else {
            this.curChar = this.source.charAt(this.curPos);
        }
    }

    // Retorna o caractere - uma previsão - verifica o próximo caractere
    public char Pesquisar() {
        if (this.curPos + 1 >= this.source.length()) {
            return '\0';
        }
        return this.source.charAt(this.curPos + 1);
    }

    // Token inválido encontrado. Apresenta mensagem de erro e sai
    public void Aborta(String message) {
        System.err.println("Erro do Analisador Lexico. " + message);
        System.exit(1);
    }

    // Ignore espaços em branco, exceto quebras de linha, que usaremos
    // para indicar o fim de uma instrução
    public void pularEspaçoBranco() {
        while (this.curChar == ' ' || this.curChar == '\t' || this.curChar == '\r') {
            this.proximoCaractere();
        }
    }

    // Ignore comentários no código
    public void pularComentário() {
        if (this.curChar == '#') {
            while (this.curChar != '\n') {
                this.proximoCaractere();
            }
        }
    }

    // Retorna o próximo TOKEN
    public Token obterToken() {
        this.pularEspaçoBranco();
        this.pularComentário();
        Token token = null;

        // Verifica o primeiro caractere do token para verificar o que é
        // Se for um operador com vários caracteres (por exemplo, !=), número, identificador ou palavra-chave,
        // processa o restante do token
        if (this.curChar == '+') {
            token = new Token(String.valueOf(this.curChar), TipoToken.PLUS);
        } else if (this.curChar == '-') {
            token = new Token(String.valueOf(this.curChar), TipoToken.MINUS);
        } else if (this.curChar == '*') {
            token = new Token(String.valueOf(this.curChar), TipoToken.ASTERISK);
        } else if (this.curChar == '/') {
            token = new Token(String.valueOf(this.curChar), TipoToken.SLASH);
        } else if (this.curChar == '=') {
            // Verifica se este token é = ou ==
            if (this.Pesquisar() == '=') {
                char lastChar = this.curChar;
                this.proximoCaractere();
                token = new Token(lastChar + String.valueOf(this.curChar), TipoToken.EQEQ);
            } else {
                token = new Token(String.valueOf(this.curChar), TipoToken.EQ);
            }
        } else if (this.curChar == '>') {
            // Verifica se este token é > ou >=
            if (this.Pesquisar() == '=') {
                char lastChar = this.curChar;
                this.proximoCaractere();
                token = new Token(lastChar + String.valueOf(this.curChar), TipoToken.GTEQ);
            } else {
                token = new Token(String.valueOf(this.curChar), TipoToken.GT);
            }
        } else if (this.curChar == '<') {
            // Verifica se este token é < ou <=
            if (this.Pesquisar() == '=') {
                char lastChar = this.curChar;
                this.proximoCaractere();
                token = new Token(lastChar + String.valueOf(this.curChar), TipoToken.LTEQ);
            } else {
                token = new Token(String.valueOf(this.curChar), TipoToken.LT);
            }
        } else if (this.curChar == '!') {
            if (this.Pesquisar() == '=') {
                char lastChar = this.curChar;
                this.proximoCaractere();
                token = new Token(lastChar + String.valueOf(this.curChar), TipoToken.NOTEQ);
            } else {
                this.Aborta("Caractere esperado !=, obtido !" + this.Pesquisar());
            }
        } else if (this.curChar == '\"') {
            // Coloca os caracteres entre aspas
            this.proximoCaractere();
            int startPos = this.curPos;
            
            while (this.curChar != '\"') {
                // Não permite caracteres especiais na String/Texto
                // Não usa caracteres tipo [\n, \t, \r, \], quebras de linha, tabulações ou %
                if (this.curChar == '\r' || this.curChar == '\n' || 
                    this.curChar == '\t' || this.curChar == '\\' || this.curChar == '%') {
                    this.Aborta("Caractere ilegal na String/Texto.");
                }
                this.proximoCaractere();
            }
            
            String tokText = this.source.substring(startPos, this.curPos); // Obtenha a substring
            token = new Token(tokText, TipoToken.STRING);
        } else if (Character.isDigit(this.curChar)) {
            // O caractere inicial é um dígito, portanto deve ser um número
            // Obtém todos os dígitos consecutivos e o decimal, se houver
            int startPos = this.curPos;
            
            while (Character.isDigit(this.Pesquisar())) {
                this.proximoCaractere();
            }
            
            if (this.Pesquisar() == '.') { // Decimal!
                this.proximoCaractere();
                
                // Deve ter pelo menos um dígito depois do decimal
                if (!Character.isDigit(this.Pesquisar())) {
                    // Erro!
                    this.Aborta("Caractere ilegal no número.");
                }
                
                while (Character.isDigit(this.Pesquisar())) {
                    this.proximoCaractere();
                }
            }
            
            String tokText = this.source.substring(startPos, this.curPos + 1); // Obtenha a substring
            token = new Token(tokText, TipoToken.NUMBER);
        } else if (Character.isLetter(this.curChar)) {
            // O caractere inicial é uma letra, portanto, deve ser um
            // identificador ou uma palavra-chave
            // Obtém todos os caracteres alfanuméricos consecutivos
            int startPos = this.curPos;
            
            while (Character.isLetterOrDigit(this.Pesquisar())) {
                this.proximoCaractere();
            }
            
            // Verifica se o token está na lista de palavras-chave
            String tokText = this.source.substring(startPos, this.curPos + 1); // Obtenha a substring
            TipoToken keyword = Token.verificarSePalavraChave(tokText);
            
            if (keyword == null) { // Identificador
                token = new Token(tokText, TipoToken.IDENT);
            } else { // Keyword
                token = new Token(tokText, keyword);
            }
        } else if (this.curChar == '\n') {
            // Newline
            token = new Token("\\n", TipoToken.NEWLINE);
        } else if (this.curChar == '\0') {
            // EOF
            token = new Token("", TipoToken.EOF);
        } else {
            // Token não conhecido!
            this.Aborta("Token desconhecido: " + this.curChar);
        }

        this.proximoCaractere();
        return token;
    }
}