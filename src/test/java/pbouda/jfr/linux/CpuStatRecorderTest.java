package pbouda.jfr.linux;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CpuStatRecorderTest {

    @Test
    void successfulParsing() {
        String content =
                "nr_periods 8480639\n" +
                "nr_throttled 38035\n" +
                "throttled_time 4645661889517\n";

        CpuStatRecorder.CpuStatEventParser parser = new CpuStatRecorder.CpuStatEventParser();
        CpuStatRecorder.CpuStatEvent event = parser.create(content);

        assertAll(
                () -> assertEquals(8_480_639, event.periods),
                () -> assertEquals(38_035, event.throttledPeriods),
                () -> assertEquals(4_645_661_889_517L, event.throttledTime));
    }

    @Test
    void successfulRepeatingParsing() {
        CpuStatRecorder.CpuStatEventParser parser = new CpuStatRecorder.CpuStatEventParser();

        // 1. parsing
        String content =
                "nr_periods 0\n" +
                "nr_throttled 0\n" +
                "throttled_time 0\n";

        CpuStatRecorder.CpuStatEvent event = parser.create(content);
        assertEquals(0, event.periods);
        assertEquals(0, event.throttledPeriods);
        assertEquals(0, event.throttledTime);

        // 2. parsing
        content =
                "nr_periods 120\n" +
                "nr_throttled 90\n" +
                "throttled_time 500000\n";

        event = parser.create(content);
        assertEquals(120, event.periods);
        assertEquals(90, event.throttledPeriods);
        assertEquals(500_000, event.throttledTime);

        // 3. parsing
        content =
                "nr_periods 150\n" +
                "nr_throttled 100\n" +
                "throttled_time 550000\n";

        event = parser.create(content);
        assertEquals(150, event.periods);
        assertEquals(100, event.throttledPeriods);
        assertEquals(550000, event.throttledTime);
    }

    @Test
    void invalidContent() {
        CpuStatRecorder.CpuStatEventParser parser = new CpuStatRecorder.CpuStatEventParser();
        Assertions.assertThrows(NumberFormatException.class, () -> parser.create("invalid-content"));
    }

    @Test
    void parserEmptyFile() {
        CpuStatRecorder.CpuStatEventParser parser = new CpuStatRecorder.CpuStatEventParser();
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class,
                () -> parser.create(""));

        Throwable noSuchElementException = runtimeException.getCause();
        assertEquals(NoSuchElementException.class, noSuchElementException.getClass());
    }
}