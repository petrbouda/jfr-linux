package pbouda.jfr.linux;

import jdk.jfr.*;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ContextSwitchesRecorder extends PeriodicalFileRecorder<ContextSwitchesRecorder.ContextSwitchesEvent> {

    private static final Path STATUS_FILE = Path.of("/proc/" + ProcessHandle.current().pid() + "/status");

    public ContextSwitchesRecorder() {
        this(STATUS_FILE);
    }

    public ContextSwitchesRecorder(Path statusFile) {
        super(statusFile, new ContextSwitchesEventParser(statusFile));
    }

    /**
     * A parser accepts the content of a particular file and parses exactly all lines and
     * creates and returns {@link ContextSwitchesEvent}.
     */
    static class ContextSwitchesEventParser implements EventParser<ContextSwitchesEvent> {

        /*
            /proc/1/status
            --------------
            ...
            Mems_allowed_list:	0
            voluntary_ctxt_switches:	1
            nonvoluntary_ctxt_switches:	0
        */

        private static final String VOLUNTARY_FIELD_NAME = "voluntary_ctxt_switches";
        private static final String NON_VOLUNTARY_FIELD_NAME = "nonvoluntary_ctxt_switches";

        private int skipLines;

        public ContextSwitchesEventParser(Path statusFile) {
            List<String> lines = LineUtils.allLines(statusFile);
            boolean voluntaryExists = LineUtils.containsLine(lines, VOLUNTARY_FIELD_NAME);
            boolean nonVoluntaryExists = LineUtils.containsLine(lines, NON_VOLUNTARY_FIELD_NAME);

            if (!voluntaryExists || !nonVoluntaryExists) {
                String message = "%s file does not contain `%s` or `%s`"
                        .formatted(STATUS_FILE.toString(), VOLUNTARY_FIELD_NAME, NON_VOLUNTARY_FIELD_NAME);
                throw new IllegalStateException(message);
            }

            int skip = 0;
            for (String line : lines) {
                if (line.startsWith(VOLUNTARY_FIELD_NAME)) {
                    this.skipLines = skip;
                }
                skip++;
            }
        }

        @Override
        public ContextSwitchesEvent create(String content) {
            try {
                Iterator<String> lines = content.lines().skip(skipLines).iterator();
                var event = new ContextSwitchesEvent();
                event.voluntary = LineUtils.longNumber(lines.next());
                event.nonVoluntary = LineUtils.longNumber(lines.next());
                return event;
            } catch (NoSuchElementException ex) {
                throw new RuntimeException("Invalid format of the source file: " + STATUS_FILE, ex);
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public Class<? extends Event> eventClass() {
        return ContextSwitchesEvent.class;
    }

    @Label("Context-switches Statistics")
    @Description("Information about a number of context-switches")
    @Category({"Linux", "ContextSwitches"})
    @Name("linux.ContextSwitches")
    @Period("1 s")
    @Registered(false)
    @Enabled(false)
    @StackTrace(false)
    static class ContextSwitchesEvent extends Event {

        @Label("Voluntary")
        @Description("Number of voluntary context-switches")
        long voluntary;

        @Label("Non-voluntary")
        @Description("Number of non-voluntary context-switches")
        long nonVoluntary;

    }
}
