package net.worph.filesytem.FileSystemLayer.wfsBasic.dto;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;

/**
 *
 * @author Worph
 */
public class WFSFileChannel extends FileChannel{
    
    protected int fileId;
    protected WFS fileSystem;
    protected long position = 0;
    protected boolean deleteOnClose = false;

    public WFSFileChannel(int fileId, WFS fileSystem, Set<? extends OpenOption> options, FileAttribute<?>... attrs) {
        this.fileId = fileId;
        this.fileSystem = fileSystem;
        doOptions(options);
    }
    
    protected final void doOptions(Set<? extends OpenOption> options){
        if(options.contains(StandardOpenOption.DELETE_ON_CLOSE)){
            deleteOnClose = true;
        }
        
        //TODO manage Option from now all file are opened in READ/WRITE Mode
        if(options.contains(StandardOpenOption.WRITE)){
            fileSystem.getWriterReader().open(fileId);
        }else if(options.contains(StandardOpenOption.READ)){
            fileSystem.getWriterReader().open(fileId);
        }else if(options.contains(StandardOpenOption.READ) && options.contains(StandardOpenOption.WRITE)){
            fileSystem.getWriterReader().open(fileId);
        }else{
            fileSystem.getWriterReader().open(fileId);
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        try {
            fileSystem.getWriterReader().setCurrsor(fileId, position);
            int read = fileSystem.getWriterReader().read(fileId,dst.array(),dst.position(),dst.remaining());
            position+=read;
            dst.position(dst.position()+read);
            return read;
        } catch (EOFException ex) {
            return -1;
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        fileSystem.getWriterReader().setCurrsor(fileId, position);
        int write = fileSystem.getWriterReader().write(fileId, src.array(),src.position(),src.remaining());
        position+=write;
        src.position(src.position()+write);
        return write;
    }    
    
    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        position=newPosition;
        return this;
    }

    @Override
    public long size() throws IOException {
        return fileSystem.getWriterReader().getSize(fileId);
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        this.position=position;
        return read(dst);
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        this.position=position;
        return write(src);
    }
    
    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public FileChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void force(boolean metaData) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void implCloseChannel() throws IOException {
        fileSystem.close(fileId);
        if(deleteOnClose){
            fileSystem.deleteFile(fileId);
        }
    }
    
}
