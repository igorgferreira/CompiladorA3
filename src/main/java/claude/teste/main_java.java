// Arquivo: src/main/java/claude/teste/main_java.java
package claude.teste;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class main_java {

    public static void main(String[] args) {
        System.out.println("Compilador pyUAM (Gerador Java)");

        if (args.length != 1) {
            System.err.println("Uso: java Main <fonte.txt>");
            System.exit(1);
        }

        String arquivo = args[0];
        Path caminhoArquivo = Paths.get(arquivo);

        if (!Files.exists(caminhoArquivo)) {
            System.err.println("Erro: arquivo não encontrado.");
            System.exit(1);
        }

        String source = "";
        try {
            source = Files.readString(caminhoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Configura o Emissor para gerar "ProgramaGerado.java"
        // Este nome DEVE corresponder ao "public class ProgramaGerado" definido no AnalisadorSintatico
        Emissor emissor = new Emissor("ProgramaGerado.java");

        AnalisadorLexico anlx = new AnalisadorLexico(source);
        AnalisadorSintatico anst = new AnalisadorSintatico(anlx, emissor);

        try {
            anst.program(); // Executa a análise e geração
            emissor.writeFile(); // Salva o arquivo .java
        } catch (Exception e) {
            System.err.println("Erro na compilação: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Compilação concluída com sucesso!");
        System.out.println("Para executar o programa gerado:");
        System.out.println("1. javac ProgramaGerado.java");
        System.out.println("2. java ProgramaGerado");
    }
}