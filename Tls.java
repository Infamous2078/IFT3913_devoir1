import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class Tls {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Format: tls [-o <chemin-à-la-sortie.csv>] <chemin-de-l'entrée>");
            return;
        }

        String outputPath = null;
        String inputPath;

        if (args[0].equals("-o")) {
            if (args.length < 3) {
                System.out.println("Il manque le Chemin d'entrée parmis les arguments");
                return;
            }
            outputPath = args[1];
            inputPath = args[2];
        } else {
            inputPath = args[0];
        }

        File inputDir = new File(inputPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Ce chemin d'entrée n'existe pas ou n'est pas un répertoire");
            return;
        }

        StringBuilder output = new StringBuilder();
        output.append("Chemin du fichier, Nom du paquet, Nom de la classe, Tloc de la classe, Tassert de la classe, Tcmp de la classe\n");

        for (File file : inputDir.listFiles()) {
            if (file.getName().endsWith(".java")) {
                String content = new String(Files.readAllBytes(file.toPath()));
                String packageName = extractPackageName(content);
                String className = extractClassName(file.getName());
                int tloc = Tloc.calculateTloc(file);
                int tassert = Tassert.calculateTassert(file);
                double val = tloc / (double) tassert;
                double tcmp = Math.floor(val * 100) / 100;

                output.append(file.getPath()).append(", ")
                      .append(packageName).append(", ")
                      .append(className).append(", ")
                      .append(tloc).append(", ")
                      .append(tassert).append(", ")
                      .append(tcmp).append("\n");
            }
        }

        if (outputPath != null) {
            try (FileWriter fw = new FileWriter(new File(outputPath))) {
                fw.write(output.toString());
            }
        } else {
            System.out.println(output);
        }
    }

    private static String extractPackageName(String content) {
        Pattern pattern = Pattern.compile("package\\s+([^;]+);");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private static String extractClassName(String fileName) {
        return fileName.replace(".java", "");
    }

}
