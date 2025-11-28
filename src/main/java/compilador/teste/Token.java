package compilador.teste;

public class Token {
    // Em Java, costumamos usar 'final' se o valor não for mudar depois de criado
    final String text;
    final TipoToken kind;

    // Construtor
    public Token(String tokenText, TipoToken tokenKind) {
        this.text = tokenText;
        this.kind = tokenKind;
    }

    // Getters para acessar os valores
    public String getText() {
        return text;
    }

    public TipoToken getKind() {
        return kind;
    }

    // Método Estático (@staticmethod)
    public static TipoToken verificarSePalavraChave(String tokenText) {
        // Percorre todos os valores do Enum (equivalente ao 'for tipo in tipotoken')
        for (TipoToken tipo : TipoToken.values()) {
            
            // 1. Verifica o nome (tipo.name())
            // Nota: Em Java usamos .equals() para comparar Strings, nunca ==
            // Se o seu código fonte for minúsculo ("if") e o Enum maiúsculo (IF), 
            // use .equalsIgnoreCase(tokenText)
            boolean nomeBate = tipo.name().equals(tokenText);
            
            // 2. Verifica o valor numérico
            // Assumindo que seu Enum tem um método getValue()
            boolean valorNoRange = tipo.getValue() >= 100 && tipo.getValue() < 200;

            if (nomeBate && valorNoRange) {
                return tipo;
            }
        }
        return null; // Retorna null se não encontrar (equivalente ao None)
    }
}