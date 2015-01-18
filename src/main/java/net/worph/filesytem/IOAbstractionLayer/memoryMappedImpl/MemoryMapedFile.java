package net.worph.filesytem.IOAbstractionLayer.memoryMappedImpl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.worph.filesytem.IOAbstractionLayer.BytePack;

/**
 *
 * @author Worph
 */
public class MemoryMapedFile implements BytePack{
    
    private MappedByteBuffer buffer;
    private FileChannel channel;

    public MemoryMapedFile(URI fileLocation,int maxSize) {
        try {
            Path path = Paths.get(fileLocation);
            channel = new RandomAccessFile(path.toFile(), "rw").getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, maxSize);
            buffer.limit(maxSize);
            buffer.position(0);
        } catch (IOException ex) {
            Logger.getLogger(MemoryMapedFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void flush(){
        buffer.force();
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException ex) {
            Logger.getLogger(MemoryMapedFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int read(ByteBuffer dst) throws IOException {
        int i = dst.remaining();
        buffer.get(dst.array(), dst.position(),i);
        return i;
    }
    
    public int read(ByteBuffer dst, long position) throws IOException {
        buffer.position((int) position);
        return read(dst);
    }

    public int write(ByteBuffer src) throws IOException {
        int i = src.remaining();
        buffer.put(src);
        return i;
    }

    public int write(ByteBuffer src, long position) throws IOException {
        buffer.position((int) position);
        return write(src);
    }

    public long position() throws IOException {
        return buffer.position();
    }

    public void position(long newPosition) throws IOException {
        buffer.position((int) newPosition);
    }

    public long capacity() throws IOException{
        return buffer.capacity();
    }

    public long size() throws IOException {
        return buffer.capacity();
    }

}
