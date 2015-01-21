package net.worph.filesytem.FileSystemLayer;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

/**
 *
 * @author Worph
 */
public interface WNameFileSystem {
    
    public void delete(URI uri) throws IOException;
    
    public void create(URI uri) throws IOException;
    
    public void rename(URI name, URI newName) throws IOException;
    
    public FileChannel newFileChannel(URI uri, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException;
        
}
