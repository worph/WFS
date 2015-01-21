package net.worph.filesytem.FileSystemLayer.wfsName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import net.worph.filesytem.FileSystemLayer.WNameFileSystem;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;
import net.worph.filesytem.IOAbstractionLayer.BytePack;

/**
 *
 * @author Worph
 */
public class WFSName extends WFS implements WNameFileSystem,WFSNameFinals{
    
    private WFSNameIndex nameIndex;

    public WFSName(BytePack storage) throws IOException {
        super(storage);
    }

    public WFSName(BytePack storage, OpenMode mode) throws IOException {
        super(storage, mode);
    }

    @Override
    protected void readFileSystem() throws IOException {
        super.readFileSystem();
        nameIndex = new WFSNameIndex(this);
        nameIndex.readNameIndex();
    }

    @Override
    protected void createFileSystem() throws IOException {
        super.createFileSystem();
        nameIndex = new WFSNameIndex(this);
        nameIndex.createNameIndex();
    }
    
    public void create(URI uri) throws IOException {
        String struri = uri.toString();
        int fileID = nameIndex.getFileId(struri);
        if(fileID==c_nameIndexFileIdNotInNameIndex){//the file doesn't exist
            fileID = createFile();//1/3
            nameIndex.addNewEntry(struri,fileID);//2/3
        }else{
            throw new IOException("File Already Exist");
        }
    }

    public void delete(URI uri) throws IOException {
        String struri = uri.toString();
        int fileID = nameIndex.getFileId(struri);
        if(fileID!=c_nameIndexFileIdNotInNameIndex){//the file doesn't exist
            nameIndex.deleteEntry(struri);
        }else{
            throw new FileNotFoundException(struri);
        }
    }
        
    @Override
    public FileChannel newFileChannel(URI uri, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        String struri = uri.toString();
        int fileID = nameIndex.getFileId(struri);
        if(fileID==c_nameIndexFileIdNotInNameIndex){
            if(options.contains(StandardOpenOption.CREATE) || options.contains(StandardOpenOption.CREATE_NEW)){//TODO CREATE
                create(uri);
                fileID = nameIndex.getFileId(struri);
            }else{
                throw new FileNotFoundException(struri);
            }
        }        
        return newFileChannel(fileID, options, attrs);
    }
    
    public void createIfNotExist(URI src) throws IOException{
        String struri = src.toString();
        int fileID = nameIndex.getFileId(struri);
        if(fileID==c_nameIndexFileIdNotInNameIndex){//the file doesn't exist
            fileID = createFile();
            nameIndex.addNewEntry(struri,fileID);
        }
    }
    
    @Override
    public void rename(URI name, URI newName) throws IOException {
        /*
         * 1 - getFileId(newName)
         * 2 - if getFileId(newName) == not found && getFileId(name) found
         *      - remove name entry from nameindex
         *      - add newName the nameindex (find a place in file)
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getFileId(URI uri) {
        return nameIndex.getFileId(uri.toString());
    }

    
}
