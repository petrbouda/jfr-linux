package pbouda.jfr.linux;

import jdk.jfr.Event;

/**
 * Reads data from an external resources, creates a JFR {@link jdk.jfr.Event}
 * and commits it to JFR buffers.
 */
public interface Recorder extends Runnable, AutoCloseable {

    /**
     * Returns a reference to a class file representing the event
     * which is handled by the current recorder.
     *
     * @return event's class.
     */
    Class<? extends Event> eventClass();
}
