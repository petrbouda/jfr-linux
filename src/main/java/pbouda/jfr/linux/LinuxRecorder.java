package pbouda.jfr.linux;

import jdk.jfr.FlightRecorder;

import java.util.Collection;
import java.util.List;

public class LinuxRecorder {

    private static final Collection<Recorder> RECORDERS = List.of(
            new CfsSettingsRecorder(),
            new CpuStatRecorder(),
            new ContextSwitchesRecorder());

    public static void start() {
        start(RECORDERS);
    }

    public static void start(Collection<Recorder> recorders) {
        for (Recorder recorder : recorders) {
            FlightRecorder.register(recorder.eventClass());
            FlightRecorder.addPeriodicEvent(recorder.eventClass(), recorder);
        }
    }
}
