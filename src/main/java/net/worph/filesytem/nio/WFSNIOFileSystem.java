package net.worph.filesytem.nio;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.worph.filesytem.FileSystemLayer.wfsName.WFSName;

/**
 *
 * @author Worph
 */
public class WFSNIOFileSystem extends FileSystem{
    
    WFSName fileSystem;
    FileSystemProvider provider;
    boolean open;
    Path root;

    public WFSNIOFileSystem(WFSName fileSystem, FileSystemProvider provider) {
        this.fileSystem = fileSystem;
        this.provider = provider;
        open = true;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        fileSystem.close();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Arrays.asList(root);
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return new HashSet<>();
    }

    @Override
    public Path getPath(String first, String... more) {
        ArrayList<String> newArray = new ArrayList<>();
        newArray.add(first);
        newArray.addAll(Arrays.asList(more));
        return new WFSNIOPath(this, newArray);
    }

    /* no */
    
    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
