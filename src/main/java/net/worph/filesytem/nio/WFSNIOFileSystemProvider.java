package net.worph.filesytem.nio;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;
import net.worph.filesytem.FileSystemLayer.wfsName.WFSName;
import net.worph.filesytem.IOAbstractionLayer.BytePack;

/**
 *
 * @author Worph
 */
public class WFSNIOFileSystemProvider extends java.nio.file.spi.FileSystemProvider{
        
    HashMap<URI,FileSystem> fschache = new HashMap<>();

    public WFSNIOFileSystemProvider() {
        
    }
    
    @Override
    public String getScheme() {
        return "wfs";
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        BytePack bytePack = (BytePack) env.get("bytepack");
        WFSName worphFileSytem = new WFSName(bytePack, WFS.OpenMode.ForceCreate);
        WFSNIOFileSystem wfsnioFileSystem = new WFSNIOFileSystem(worphFileSytem,this);
        fschache.put(uri, wfsnioFileSystem);
        return wfsnioFileSystem;
        
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        return fschache.get(uri);
    }

    @Override
    public Path getPath(URI uri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return newFileChannel(path, options, attrs);
    }

    @Override
    public FileChannel newFileChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        FileSystem fileSystem = path.getFileSystem();
        if(fileSystem instanceof WFSNIOFileSystem){
            WFSNIOFileSystem fSNIOFileSystem = (WFSNIOFileSystem) fileSystem;
            return fSNIOFileSystem.fileSystem.newFileChannel(path.toUri(), options, attrs);
        }
        throw new UnsupportedOperationException("Not supported yet. : "+fileSystem.getClass());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Path path) throws IOException {
        FileSystem fileSystem = path.getFileSystem();
        if(fileSystem instanceof WFSNIOFileSystem){
            WFSNIOFileSystem fSNIOFileSystem = (WFSNIOFileSystem) fileSystem;
            fSNIOFileSystem.fileSystem.delete(path.toUri());
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        /*FileSystem fileSystemSource = source.getFileSystem();
        FileSystem fileSystemTarget = source.getFileSystem();
        if(fileSystemSource instanceof WFSNIOFileSystem && fileSystemTarget instanceof WFSNIOFileSystem){
            WFSNIOFileSystem fSNIOFileSystem = (WFSNIOFileSystem) fileSystemSource;
            fSNIOFileSystem.fileSystem.copy(source.toUri(), target.toUri());
        }*/
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        /*FileSystem fileSystemSource = source.getFileSystem();
        FileSystem fileSystemTarget = source.getFileSystem();
        if(fileSystemSource instanceof WFSNIOFileSystem && fileSystemTarget instanceof WFSNIOFileSystem){
            WFSNIOFileSystem fSNIOFileSystem = (WFSNIOFileSystem) fileSystemSource;
            fSNIOFileSystem.fileSystem.move(source.toUri(), target.toUri());
        }*/
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return path.compareTo(path2) == 0;
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
