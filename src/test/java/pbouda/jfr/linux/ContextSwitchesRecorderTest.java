package pbouda.jfr.linux;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ContextSwitchesRecorderTest {

    @Test
    public void parser() throws URISyntaxException, IOException {
        URL status = ContextSwitchesRecorderTest.class.getResource("/status");
        Path statusFile = Paths.get(status.toURI());
        var parser = new ContextSwitchesRecorder.ContextSwitchesEventParser(statusFile);
        ContextSwitchesRecorder.ContextSwitchesEvent event = parser.create(Files.readString(statusFile));

        assertEquals(123, event.voluntary);
        assertEquals(0, event.nonVoluntary);
    }

}