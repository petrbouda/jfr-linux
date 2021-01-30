package pbouda.jfr.linux;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileDataReaderTest {

    @Test
    void successfulRead() throws Exception {
        Path cfsQuota = FileUtils.createTempFile("cpu.cfs_quota_us");
        Files.write(cfsQuota, "20000\n".getBytes());

        DataReader reader = new FileDataReader(cfsQuota);
        String content = reader.read();
        assertEquals("20000", content);
    }

    @Test
    void repeatableRead() throws Exception {
        Path cfsQuota = FileUtils.createTempFile("cpu.cfs_quota_us");
        Files.write(cfsQuota, "20000\n".getBytes());

        DataReader reader = new FileDataReader(cfsQuota);
        String content = reader.read();
        assertEquals("20000", content);

        content = reader.read();
        assertEquals("20000", content);
    }

    @Test
    void emptyFile() throws Exception {
        Path cfsQuota = FileUtils.createTempFile("cpu.cfs_quota_us");

        DataReader reader = new FileDataReader(cfsQuota);
        String content = reader.read();
        assertEquals("", content);
    }

    @Test
    void nonExistingFile() {
        DataReader reader = new FileDataReader(Path.of("/tmp/non-existing"));
        Assertions.assertThrows(NoSuchFileException.class, reader::read);
    }
}