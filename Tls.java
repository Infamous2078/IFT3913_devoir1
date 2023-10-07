import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

public class Tls {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Format: Tls [-o <chemin-à-la-sortie.csv>] <chemin-de-l'entrée>");
            return;
        }

        String outputPath = null;
        String inputPath;

        if (args[1].equals("-o")) {
            outputPath = args[2];
            inputPath = args[3];
        } else {
            inputPath = args[1];
        }

        File inputDir = new File(inputPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Ce chemin d'entrée n'existe pas ou n'est pas un répertoire");
            return;
        }

        String output = getOutput(inputDir, null);

        if (outputPath != null) {
            try (FileWriter fw = new FileWriter(outputPath)) {
                fw.write(output);
            }
        } else {
            System.out.println(output);
        }
    }

    public static String getOutput(File inputDir, List<File> list) throws IOException {
        StringBuilder output = new StringBuilder();
        output.append("Chemin du fichier, Nom du paquet, Nom de la classe, Tloc de la classe, Tassert de la classe, Tcmp de la classe\n");
        List<File> fileList;
        if (inputDir != null && list == null) {
            fileList = List.of(inputDir.listFiles());
        }
        else {
            fileList = list;
        }

        for (File file : fileList) {
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
        return output.toString();
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
