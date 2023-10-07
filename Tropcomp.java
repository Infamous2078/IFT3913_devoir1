import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tropcomp {

    private static class TestFileMetrics {
        private String filePath;
        private double tloc;
        private double tcmp;

        public TestFileMetrics(String filePath, double tloc, double tassert) {
            this.filePath = filePath;
            this.tloc = tloc;
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
    }

    public static List<String> findTopSuspectFiles(String directoryPath, double threshold) throws IOException {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        List<TestFileMetrics> metricsList = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith("Test.java")) {
                int tloc = Tloc.countLines(file.getPath());
                int tassert = Tassert.countAssertions(file.getPath());
                metricsList.add(new TestFileMetrics(file.getPath(), tloc, tassert));
            }
        }

        List<TestFileMetrics> topTLOCFiles = getTopFilesByTLOC(metricsList, threshold);
        List<TestFileMetrics> topTCMPFiles = getTopFilesByTCMP(metricsList, threshold);

        List<String> suspectFiles = new ArrayList<>();
        for (TestFileMetrics metric : metricsList) {
            if (topTLOCFiles.contains(metric) && topTCMPFiles.contains(metric)) {
                suspectFiles.add(metric.getFilePath());
            }
        }

        return suspectFiles;
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

    public static void main(String[] args) throws IOException {
        List<String> suspectFiles = findTopSuspectFiles("C:\\Users\\maseu\\Desktop\\CoursA23\\IFT3913\\IFT3913_devoir1\\src\\testFiles", 0.2);
        for (String file : suspectFiles) {
            System.out.println(new File(file).getName());
        }
    }
}

