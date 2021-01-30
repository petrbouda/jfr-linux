package pbouda.jfr.linux;

import jdk.jfr.*;

import java.nio.file.Path;

/**
 * Opens two files and parses single-integer values. This is one-shot operation.
 * There is no need to publish those values to JFR again.
 * <ul>
 * <li>{@link #CFS_PERIOD_FILENAME}</li>
 * <li>{@link #CFS_QUOTA_FILENAME}</li>
 * <ul/>
 */
public class CfsSettingsRecorder implements Recorder {

    private static final String CFS_PERIOD_FILENAME = "cpu.cfs_period_us";
    private static final String CFS_QUOTA_FILENAME = "cpu.cfs_quota_us";

    private final Path periodFile;
    private final Path quotaFile;

    public CfsSettingsRecorder() {
        this(DefaultFolders.CGROUPS_CPU);
    }

    public CfsSettingsRecorder(Path baseDir) {
        this(baseDir.resolve(CFS_PERIOD_FILENAME), baseDir.resolve(CFS_QUOTA_FILENAME));
    }

    public CfsSettingsRecorder(Path periodFile, Path quotaFile) {
        this.periodFile = periodFile;
        this.quotaFile = quotaFile;
    }

    @Override
    public void run() {
        Event event = createEvent(periodFile, quotaFile);
        event.commit();
    }

    /*
     * package-private only for testing purposes. Change when you find better
     * way to test storing JFR events.
     */
    static CfsSettingsEvent createEvent(Path periodFile, Path quotaFile) {
        String periodContent;
        String quotaContent;
        try (DataReader periodReader = new FileDataReader(periodFile);
             DataReader quotaReader = new FileDataReader(quotaFile)) {
            periodContent = periodReader.read();
            quotaContent = quotaReader.read();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot read CFS Settings", ex);
        }

        CfsSettingsEvent event = new CfsSettingsEvent();
        event.periodDuration = Integer.parseInt(periodContent);
        event.quotaDuration = Integer.parseInt(quotaContent);
        return event;
    }

    @Override
    public Class<? extends Event> eventClass() {
        return CfsSettingsEvent.class;
    }

    @Override
    public void close() {
    }

    @Label("CFS Settings")
    @Description("Period and Quota duration for Completely Fair Scheduler")
    @Category({"Linux", "Cgroups", "CPU"})
    @Name("linux.CfsSettings")
    @Period("everyChunk")
    @Registered(false)
    @Enabled(false)
    @StackTrace(false)
    static class CfsSettingsEvent extends Event {

        @Label("Quota Duration")
        long quotaDuration;

        @Label("Period Duration")
        long periodDuration;
    }
}
