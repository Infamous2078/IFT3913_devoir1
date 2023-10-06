import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Tloc {

    public static void main(String[] args) throws IOException{
        if (args.length != 1) {
            System.out.println("Format: java tloc <fichier>");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists() || !file.isFile()) {
            System.out.println(file.getName() + " n'existe pas ou n'est pas un fichier.");
            return;
        }

        System.out.println(calculateTloc(file));

    }

    public static int calculateTloc(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int tloc = 0;

        boolean isMultilineComment = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (!line.isEmpty()) {
                if (isMultilineComment) {
                    if (line.endsWith("*/")) {
                        isMultilineComment = false;
                    }
                    continue;
                } else if (line.startsWith("/*")) {
                    isMultilineComment = true;
                    if (line.endsWith("*/")) {
                        isMultilineComment = false;
                    }
                    continue;
                } else if (line.startsWith("//")) {
                    continue;
                }
                tloc++;
            }
        }
        reader.close();

        return tloc;
    }

}
