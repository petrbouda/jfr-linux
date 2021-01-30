package pbouda.jfr.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {



    public static Path createTempFile(String filename, String content) {
        try {
            Path cpuStat = createTempFile(filename);
            return Files.write(cpuStat, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path createTempFile(String filename) {
        try {
            Path jfrDir = Files.createTempDirectory("jfr");
            return Files.createFile(jfrDir.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
