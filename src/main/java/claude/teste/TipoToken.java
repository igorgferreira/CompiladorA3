package claude.teste;

// TipoToken é nosso enum para todos os tipos de tokens
public enum TipoToken {
    // Marca o fim do código
    EOF(-1),
    
    // Uma linha
    NEWLINE(0),
    
    // Identifica números literais
    NUMBER(1),
    
    // Identifica nomes de variáveis ou funções
    IDENT(2),
    
    // Identifica textos entre aspas
    STRING(3),
    
    // Palavra-chave
    
    // Marca um ponto no código (para usar com GOTO)
    LABEL(101),
    
    // Salta para um LABEL específico
    GOTO(102),
    
    // Exibe um valor na tela
    PRINT(103),
    
    // Lê um valor do usuário
    INPUT(104),
    
    // Declara ou atribui um valor a uma variável
    LET(105),
    
    // Início de uma condição
    IF(106),
    
    // Parte do IF — executa se a condição for verdadeira
    THEN(107),
    
    // Marca o fim do bloco IF
    ENDIF(108),
    
    // Início de um laço de repetição
    WHILE(109),
    
    // Corpo de repetição do WHILE
    REPEAT(110),
    
    // Fim do laço WHILE
    ENDWHILE(111),
    
    // Operador
    
    // =
    EQ(201),
    
    // +
    PLUS(202),
    
    // -
    MINUS(203),
    
    // *
    ASTERISK(204),
    
    // /
    SLASH(205),
    
    // ==
    EQEQ(206),
    
    // !=
    NOTEQ(207),
    
    // <
    LT(208),
    
    // <=
    LTEQ(209),
    
    // >
    GT(210),
    
    // >=
    GTEQ(211);
    
    private final int value;
    
    // Construtor do enum
    TipoToken(int value) {
        this.value = value;
    }
    
    // Getter para obter o valor numérico
    public int getValue() {
        return this.value;
    }
}