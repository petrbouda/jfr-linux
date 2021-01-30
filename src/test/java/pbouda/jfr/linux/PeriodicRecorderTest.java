package pbouda.jfr.linux;

import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PeriodicRecorderTest {

    @Test
    void periodicRecording() {
        String content =
                "nr_periods 1\n" +
                "nr_throttled 2\n" +
                "throttled_time 3\n";
        Path cpuStat = FileUtils.createTempFile("cpu.stat", content);

        CpuStatRecorder cpuStatRecorder = new CpuStatRecorder(cpuStat.getParent());
        List<RecordedEvent> recordedEvents = RecordingUtils.run(
                cpuStatRecorder, Duration.ofMillis(1500), Duration.ofMillis(100));

        List<CpuStatRecorder.CpuStatEvent> events = recordedEvents.stream()
                .peek(System.out::println)
                .map(PeriodicRecorderTest::toCpuStatEvent)
                .collect(Collectors.toUnmodifiableList());

        var cpu1 = new CpuStatRecorder.CpuStatEvent();
        cpu1.periods = 1;
        cpu1.throttledPeriods = 2;
        cpu1.throttledTime = 3;

        var cpu2 = new CpuStatRecorder.CpuStatEvent();
        cpu2.periods = 1;
        cpu2.throttledPeriods = 2;
        cpu2.throttledTime = 3;

        assertTrue(events.size() > 2);
        assertEquals(cpu1.periods, events.get(0).periods);
        assertEquals(cpu1.throttledPeriods, events.get(0).throttledPeriods);
        assertEquals(cpu1.throttledTime, events.get(0).throttledTime);

        assertEquals(cpu2.periods, events.get(1).periods);
        assertEquals(cpu2.throttledPeriods, events.get(1).throttledPeriods);
        assertEquals(cpu2.throttledTime, events.get(1).throttledTime);
        assertNotSame(events.get(0), events.get(1));
    }

    private static CpuStatRecorder.CpuStatEvent toCpuStatEvent(RecordedEvent event) {
        CpuStatRecorder.CpuStatEvent cpuStatEvent = new CpuStatRecorder.CpuStatEvent();
        cpuStatEvent.periods = event.getValue("periods");
        cpuStatEvent.throttledPeriods = event.getValue("throttledPeriods");
        cpuStatEvent.throttledTime = event.getValue("throttledTime");
        return cpuStatEvent;
    }
}