package pbouda.jfr.linux;

import jdk.jfr.Event;

import java.nio.file.Path;

/**
 * A skeleton for periodical reading from a file, parsing the values
 * and creates events of the particular type {@link T}.
 *
 * @param <T> extends {@link Event}
 */
abstract class PeriodicalFileRecorder<T extends Event> implements Recorder {

    private final DataReader reader;
    private final EventParser<T> parser;
    private final Path path;

    PeriodicalFileRecorder(Path path, EventParser<T> parser) {
        this.reader = new FileDataReader(path);
        this.path = path;
        this.parser = parser;
    }

    @Override
    public void run() {
        try {
            String content = reader.read();
            T event = parser.create(content);
            event.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Could not get content of " + path);
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
