package pbouda.jfr.linux;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CfsSettingsRecorderTest {

    @Test
    void commitCfsSettingsEvent() throws IOException {
        Path cfsQuota = FileUtils.createTempFile("cpu.cfs_quota_us");
        Path cfsPeriod = FileUtils.createTempFile("cpu.cfs_period_us");

        Files.write(cfsPeriod, "100000\n".getBytes());
        Files.write(cfsQuota, "20000\n".getBytes());

        CfsSettingsRecorder.CfsSettingsEvent event = CfsSettingsRecorder.createEvent(cfsPeriod, cfsQuota);

        assertAll(
                () -> assertEquals(100_000, event.periodDuration),
                () -> assertEquals(20_000, event.quotaDuration));
    }

    @Test
    void parsingNumberError() throws IOException {
        Path cfsQuota = FileUtils.createTempFile("cpu.cfs_quota_us");
        Path cfsPeriod = FileUtils.createTempFile("cpu.cfs_period_us");

        Files.write(cfsPeriod, "invalid-integer-value\n".getBytes());
        Files.write(cfsQuota, "20000\n".getBytes());

        Assertions.assertThrows(NumberFormatException.class, () -> CfsSettingsRecorder.createEvent(cfsPeriod, cfsQuota));
    }

    @Test
    void fileDoesNotExist() {
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class,
                () -> CfsSettingsRecorder.createEvent(Path.of("/tmp/invalid-path"), Path.of("/tmp/invalid-path")));

        Throwable noSuchFileException = runtimeException.getCause();
        assertEquals(NoSuchFileException.class, noSuchFileException.getClass());
    }

    @Test
    void emptyFile() throws IOException {
        Path cfsQuota = FileUtils.createTempFile("cpu.cfs_quota_us");
        Path cfsPeriod = FileUtils.createTempFile("cpu.cfs_period_us");

        Files.write(cfsPeriod, "100000\n".getBytes());
        // Intentionally empty
        // Files.write(cfsQuota, "20000".getBytes());

        Assertions.assertThrows(NumberFormatException.class, () -> CfsSettingsRecorder.createEvent(cfsPeriod, cfsQuota));
    }
}