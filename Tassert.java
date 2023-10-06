import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Tassert {
    public static void main(String[] args) throws IOException{
        if (args.length != 1) {
            System.out.println("Format: java tloc <fichier>");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Ce <fileName> n'existe pas ou n'est pas un fichier.");
            return;
        }

        System.out.println(calculateTassert(file));
    }


    public static int calculateTassert(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int tassert = 0;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (line.startsWith("assert")) {
                tassert++;
                continue;
                }
            }
        }
        reader.close();
        
        return tassert; 
    }
    
}
