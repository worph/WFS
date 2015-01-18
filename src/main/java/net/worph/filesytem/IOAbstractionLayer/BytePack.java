package net.worph.filesytem.IOAbstractionLayer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Worph
 */
public interface BytePack extends Closeable{
            
    @Override
    public void close()throws IOException;

    public void flush()throws IOException;
    
    public int read(ByteBuffer dst) throws IOException;
    
    public int read(ByteBuffer dst, long position) throws IOException ;

    public int write(ByteBuffer src) throws IOException;
   
    public int write(ByteBuffer src, long position) throws IOException;
    
    public long position() throws IOException ;
    
    public void position(long newPosition) throws IOException;
    
    public long capacity() throws IOException;
    
    public long size() throws IOException;
    
}
