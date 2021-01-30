package pbouda.jfr.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class LineUtils {

    // voluntary_ctxt_switches:	    1
    // nonvoluntary_ctxt_switches:	0
    static long longNumber(String line) {
        String[] parts = line.split(":");
        String number = parts[1].stripLeading();
        return Long.parseLong(number);
    }

    static boolean containsLine(List<String> lines, String fieldName) {
        return lines.stream().anyMatch(line -> line.startsWith(fieldName));
    }

    static List<String> allLines(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException ex) {
            throw new RuntimeException("Invalid format of the source file: " + file.toString(), ex);
        }
    }
}
