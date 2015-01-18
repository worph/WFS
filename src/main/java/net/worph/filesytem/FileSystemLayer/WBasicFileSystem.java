package net.worph.filesytem.FileSystemLayer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

/**
 *
 * @author Worph
 */
public interface WBasicFileSystem extends Closeable{
        
    public FileChannel newFileChannel(int fileid, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException;
        
    public void flushCache();
    
}
