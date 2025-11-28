// Arquivo: src/main/java/claude/teste/Emissor.java
package compilador.teste;

import java.io.FileWriter;
import java.io.IOException;

public class Emissor {
    private String fullPath;
    private String header;
    private String code;

    public Emissor(String fullPath) {
        this.fullPath = fullPath;
        this.header = "";
        this.code = "";
    }

    public void emit(String code) {
        this.code += code;
    }

    public void emitLine(String code) {
        this.code += code + "\n";
    }

    public void headerLine(String code) {
        this.header += code + "\n";
    }

    public void writeFile() {
        try (FileWriter myWriter = new FileWriter(fullPath)) {
            myWriter.write(header + code);
            System.out.println("Código Java gerado salvo em: " + fullPath);
        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao gravar o arquivo de saída.");
            e.printStackTrace();
        }
    }
}