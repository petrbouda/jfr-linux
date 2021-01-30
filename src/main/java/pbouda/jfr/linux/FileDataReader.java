package pbouda.jfr.linux;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * Dumps the content of the file into a byte array without
 * opening of file-descriptors for every operation.
 * <p>
 * This implementation is <b>not thread-safe</b>.
 */
public class FileDataReader implements DataReader {

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private final Path file;
    private final ByteBuffer buffer;
    private FileChannel channel;

    public FileDataReader(Path file) {
        this.file = file;
        this.buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    }

    @Override
    public String read() throws Exception {
        buffer.clear();
        if (channel == null) {
            this.channel = (FileChannel) Files.newByteChannel(
                    this.file, EnumSet.of(StandardOpenOption.READ));
        }

        // TODO: Resize the buffer if the file is bigger than DEFAULT_BUFFER_SIZE
        int bytes = channel.read(buffer, 0);
        // Remove one byte - new-line
        return bytes == -1 ? "" : new String(buffer.array(), 0, bytes - 1, StandardCharsets.US_ASCII);
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }
}
