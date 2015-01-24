package net.worph.filesytem.nio;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Worph
 */
public class WFSNIOPath implements Path{
    
    WFSNIOFileSystem fileSystem;
    ArrayList<String> items;

    public WFSNIOPath(WFSNIOFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        items = new ArrayList<>();
    }

    public WFSNIOPath(WFSNIOFileSystem fileSystem, ArrayList<String> items) {
        this.fileSystem = fileSystem;
        this.items = items;
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public boolean isAbsolute() {
        for(String str : items){
            if(str.equals("..") || str.equals(".")){
                return false;
            }
        }
        return true;
    }

    @Override
    public Path getRoot() {
        return fileSystem.getPath(fileSystem.getSeparator());
    }

    @Override
    public Path getFileName() {
        if(items.isEmpty()) {
            return null;
        }
        return fileSystem.getPath(items.get(items.size()-1));
    }

    @Override
    public Path getParent() {
        ArrayList<String> newArray = new ArrayList<>(items.subList(0, items.size()-2));
        return new WFSNIOPath(fileSystem, newArray);
    }

    @Override
    public int getNameCount() {
        return items.size();
    }

    @Override
    public Path getName(int index) {
        if(items.isEmpty()) {
            return null;
        }
        return fileSystem.getPath(items.get(index));
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        ArrayList<String> newArray = new ArrayList<>(items.subList(beginIndex, endIndex));
        return new WFSNIOPath(fileSystem, newArray);
    }

    @Override
    public boolean startsWith(Path other) {
        if(other instanceof WFSNIOPath){
            WFSNIOPath otherWFSPath = (WFSNIOPath) other;
            if(items.size()>=otherWFSPath.items.size()){
                for(int i = 0;i<otherWFSPath.items.size();i++){
                    if(!otherWFSPath.items.get(i).equals(items.get(i))){
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean startsWith(String other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean endsWith(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean endsWith(String other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path normalize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolve(Path other) {
        if(other instanceof WFSNIOPath){
            WFSNIOPath otherWFSPath = (WFSNIOPath) other;
            ArrayList<String> newArrayList = (ArrayList<String>) items.clone();
            newArrayList.addAll(otherWFSPath.items);
            return new WFSNIOPath(fileSystem, newArrayList);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolve(String other) {        
            return resolve(fileSystem.getPath(other));
    }

    @Override
    public Path resolveSibling(Path other) {
        if(other instanceof WFSNIOPath){
            WFSNIOPath otherWFSPath = (WFSNIOPath) other;
            ArrayList<String> newArrayList = (ArrayList<String>) items.clone();
            newArrayList.remove(newArrayList.size()-1);//get parent
            newArrayList.addAll(otherWFSPath.items);
            return new WFSNIOPath(fileSystem, newArrayList);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolveSibling(String other) {
            return resolveSibling(fileSystem.getPath(other));
    }

    @Override
    public Path relativize(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URI toUri() {
        StringBuilder stringBuilder = new StringBuilder(fileSystem.provider().getScheme());
        stringBuilder.append(":");
        stringBuilder.append(fileSystem.getSeparator());
        for (String string : items) {
            stringBuilder.append(string);
            stringBuilder.append(fileSystem.getSeparator());
        }
        stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
        return URI.create(stringBuilder.toString());
    }

    @Override
    public Path toAbsolutePath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File toFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Path> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
