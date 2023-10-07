import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tropcomp {

    private static class TestFileMetrics {
        private String filePath;
        private double tloc;
        private double tcmp;
        private File file;

        public TestFileMetrics(String filePath, double tloc, double tassert, File file) {
            this.filePath = filePath;
            this.tloc = tloc;
            this.file = file;
            if (tassert != 0) {
                this.tcmp = tloc / tassert;
            }
            else {
                this.tcmp = 0;
            }
        }

        public String getFilePath() {
            return filePath;
        }

        public double getTcmp() {
            return tcmp;
        }

        public double getTloc() {
            return tloc;
        }

        public File getFile() {
            return file;
        }
    }

    public static void executeTropcomp(String[] args) throws IOException {
        if (args.length != 5 && args.length != 3) {
            System.out.println("Format: tropcomp [-o <chemin-à-la-sortie.csv>] <chemin-de-l'entrée> <seuil>");
            return;
        }

        String outputPath = null;
        String inputPath;
        double seuil;

        if (args[1].equals("-o")) {
            outputPath = args[2];
            inputPath = args[3];
            seuil = Double.parseDouble(args[4]) / 100;
        }
        else {
            inputPath = args[1];
            seuil = Double.parseDouble(args[2]) / 100;
        }

        File inputDir = new File(inputPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Ce chemin d'entrée n'existe pas ou n'est pas un répertoire");
            return;
        }

        List<File> suspectFiles = findSuspectFiles(inputDir.getAbsolutePath(), seuil);

        String output = Tls.getOutput(null, suspectFiles);

        if (outputPath != null) {
            try (FileWriter fw = new FileWriter(outputPath)) {
                fw.write(output);
            }
        }
        else {
            System.out.println(output);
        }
    }

    public static List<File> findSuspectFiles(String directoryPath, double seuil) throws IOException {
        List<TestFileMetrics> metricsList = gatherMetrics(new File(directoryPath));

        List<TestFileMetrics> topTLOCFiles = getTopFilesByTLOC(metricsList, seuil);
        List<TestFileMetrics> topTCMPFiles = getTopFilesByTCMP(metricsList, seuil);

        List<File> suspectFiles = new ArrayList<>();
        for (TestFileMetrics metric : metricsList) {
            if (topTLOCFiles.contains(metric) && topTCMPFiles.contains(metric)) {
                suspectFiles.add(metric.getFile());
            }
        }

        return suspectFiles;
    }

    private static List<TestFileMetrics> gatherMetrics(File directory) throws IOException {
        List<TestFileMetrics> metricsList = new ArrayList<>();

        File[] files = directory.listFiles();

        if (files == null)
            return metricsList;

        for (File file : files) {
            if (file.isDirectory()) {
                metricsList.addAll(gatherMetrics(file));
            }
            else if (file.getName().endsWith("Test.java")) {
                int tloc = Tloc.calculateTloc(file);
                int tassert = Tassert.calculateTassert(file);
                metricsList.add(new TestFileMetrics(file.getPath(), tloc, tassert, file));
            }
        }

        return metricsList;
    }

    private static List<TestFileMetrics> getTopFilesByTLOC(List<TestFileMetrics> metrics, double threshold) {
        List<TestFileMetrics> sortedList = new ArrayList<>(metrics);
        int countToSelect = (int) (metrics.size() * threshold);

        for (int i = 0; i < sortedList.size() - 1; i++) {
            for (int j = i + 1; j < sortedList.size(); j++) {
                if (sortedList.get(i).getTloc() < sortedList.get(j).getTloc()) {
                    TestFileMetrics temp = sortedList.get(i);
                    sortedList.set(i, sortedList.get(j));
                    sortedList.set(j, temp);
                }
            }
        }

        return sortedList.subList(0, countToSelect);
    }

    private static List<TestFileMetrics> getTopFilesByTCMP(List<TestFileMetrics> metrics, double threshold) {
        List<TestFileMetrics> sortedList = new ArrayList<>(metrics);
        int countToSelect = (int) (metrics.size() * threshold);

        for (int i = 0; i < sortedList.size() - 1; i++) {
            for (int j = i + 1; j < sortedList.size(); j++) {
                if (sortedList.get(i).getTcmp() < sortedList.get(j).getTcmp()) {
                    TestFileMetrics temp = sortedList.get(i);
                    sortedList.set(i, sortedList.get(j));
                    sortedList.set(j, temp);
                }
            }
        }

        return sortedList.subList(0, countToSelect);
    }
}
