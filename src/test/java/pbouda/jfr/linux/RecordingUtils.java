package pbouda.jfr.linux;

import jdk.jfr.Event;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public final class RecordingUtils {

    public static <T extends Event> List<RecordedEvent> run(Recorder recorder, Duration delay, Duration period) {
        LinuxRecorder.start(List.of(recorder));

        Path jfr = FileUtils.createTempFile("events.jfr");
        try (Recording recording = new Recording()) {
            recording.setName("Test");
            recording.start();
            recording.enable(recorder.eventClass()).withPeriod(period);

            Thread.sleep(delay.toMillis());

            recording.stop();
            recording.dump(jfr);
            return RecordingFile.readAllEvents(jfr);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
