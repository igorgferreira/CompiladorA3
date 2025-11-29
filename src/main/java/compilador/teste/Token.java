package compilador.teste;

public class Token {
    final String text;
    final TipoToken kind;

    // Construtor
    public Token(String tokenText, TipoToken tokenKind) {
        this.text = tokenText;
        this.kind = tokenKind;
    }

    // Getters
    public String getText() {
        return text;
    }

    public TipoToken getKind() {
        return kind;
    }

    public static TipoToken verificarSePalavraChave(String tokenText) {
        for (TipoToken tipo : TipoToken.values()) {
            
            boolean nomeBate = tipo.name().equals(tokenText);
            
            boolean valorNoRange = tipo.getValue() >= 100 && tipo.getValue() < 200;

            if (nomeBate && valorNoRange) {
                return tipo;
            }
        }
        return null;
    }
}