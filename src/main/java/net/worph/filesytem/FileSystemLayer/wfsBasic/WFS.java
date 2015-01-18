package net.worph.filesytem.FileSystemLayer.wfsBasic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.worph.filesytem.FileSystemLayer.WBasicFileSystem;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFileChannel;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFinals;
import net.worph.filesytem.IOAbstractionLayer.BytePack;

/**
 *
 * @author Worph
 */
public class WFS implements WBasicFileSystem, WFSFinals {

    public WFSWriterReader getWriterReader() {
        return writerReader;
    }

    public void setWriterReader(WFSWriterReader writerReader) {
        this.writerReader = writerReader;
    }

    public static enum OpenMode {CreateIfNotExsist,ForceCreate,Read};

    /* 
     * strorage abstraction 
     */
    private BytePack storage;

    /* 
     * cache 
     */
    protected WFSFragmentsTable fragentsTable;
    private WFSWriterReader writerReader;

    private void init(BytePack storage){
        this.storage = storage;
        fragentsTable = new WFSFragmentsTable(storage);
        writerReader = new WFSWriterReader(storage,fragentsTable);
        
    }
    //Note: storage is considered infinite
    public WFS(BytePack storage) throws IOException {
        init(storage);
        readFileSystemConstructor();
    }
    
    private void readFileSystemConstructor() throws IOException{
        readFileSystem();
    }
    
    public WFS(BytePack storage,OpenMode mode) throws IOException {
        init(storage);
        modeFileSystemConstructor(mode);
    }
    
    private void modeFileSystemConstructor(OpenMode mode) throws IOException{
         switch(mode){
            case ForceCreate:
                createFileSystem();
                break;
            case Read:
                readFileSystem();
                break;
            default:
                throw new UnsupportedOperationException("not implemented yet");    
        }
    }

    /* 
     * create and read table 
     */
    protected void readFileSystem()throws IOException{
         fragentsTable.readTable();
    }
    

    protected void createFileSystem() throws IOException{
         fragentsTable.allocateNewTableFragment();
    }

    public int createFile() throws IOException{
        return fragentsTable.allocateNewFileId();
    }
   
    public void deleteFile(int fileid) throws IOException{
        fragentsTable.deallocateEntry(fileid);
    }
            
    @Override
    public FileChannel newFileChannel(int fileid, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if(fileid==c_fileNotAllocated){
            throw new FileNotFoundException();
        }
        return new WFSFileChannel(fileid, this,options,attrs);
    }
    
    public boolean close(int fileid) throws IOException{
        writerReader.close(fileid);
        return true;
    }

    public void flushCache() {
        try {
            storage.flush();
        } catch (IOException ex) {
            Logger.getLogger(WFS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            storage.flush();
            storage.close();
        } catch (IOException ex) {
            Logger.getLogger(WFS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
