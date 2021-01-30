package pbouda.jfr.linux;

import jdk.jfr.*;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <b>This implementation is not thread-safe<b/>
 * <p>
 * Opens a file {@link #CPU_STAT_FILENAME} containing 3 lines, parses values and fills up {@link CpuStatEvent}.
 * <pre>
 * <code>
 * nr_periods 8480639
 * nr_throttled 38035
 * throttled_time 4645661889517
 * </code>
 * </pre>
 */
public class CpuStatRecorder extends PeriodicalFileRecorder<CpuStatRecorder.CpuStatEvent> {

    private static final String CPU_STAT_FILENAME = "cpu.stat";

    public CpuStatRecorder() {
        this(DefaultFolders.CGROUPS_CPU);
    }

    public CpuStatRecorder(Path baseDir) {
        super(baseDir.resolve(CPU_STAT_FILENAME), new CpuStatEventParser());
    }

    /**
     * A parser accepts the content of a particular file and parses exactly all lines and
     * creates and returns {@link CpuStatEvent}.
     */
    static class CpuStatEventParser implements EventParser<CpuStatEvent> {

        // "nr_periods "
        private static final int PERIODS_BYTES_COUNT = 11;
        // "nr_throttled "
        private static final int THROTTLED_PERIODS_BYTES_COUNT = 13;
        // "throttled_time "
        private static final int THROTTLED_TIME_BYTES_COUNT = 15;
        @Override
        public CpuStatEvent create(String content) {
            try {
                Iterator<String> lines = content.lines().iterator();
                CpuStatEvent event = new CpuStatEvent();
                event.periods = parse(lines.next(), PERIODS_BYTES_COUNT);
                event.throttledPeriods = parse(lines.next(), THROTTLED_PERIODS_BYTES_COUNT);
                event.throttledTime = parse(lines.next(), THROTTLED_TIME_BYTES_COUNT);
                return event;
            } catch (NoSuchElementException ex) {
                throw new RuntimeException("Invalid format of the source file: " + CPU_STAT_FILENAME, ex);
            }
        }

        private static long parse(String line, int prefix) {
            String value = line.substring(prefix);
            return Long.parseLong(value);
        }

    }

    @Override
    public Class<? extends Event> eventClass() {
        return CpuStatEvent.class;
    }

    @Label("CPU Statistics")
    @Description("Information about a number of periods and throttling")
    @Category({"Linux", "Cgroups", "CPU"})
    @Name("linux.CpuStat")
    @Period("1 s")
    @Registered(false)
    @Enabled(false)
    @StackTrace(false)
    static class CpuStatEvent extends Event {

        @Label("Periods")
        @Description("Number of periods")
        long periods;

        @Label("Throttled periods")
        @Description("Number of periods that ended up throttled")
        long throttledPeriods;

        @Label("Throttled time")
        @Description("Amount of throttled time")
        @Timespan(Timespan.NANOSECONDS)
        long throttledTime;
    }
}
