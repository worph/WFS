package net.worph.testvfs.wfsBasic;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;
import net.worph.filesytem.IOAbstractionLayer.BytePack;
import net.worph.filesytem.IOAbstractionLayer.memoryMappedImpl.MemoryMapedFile;

/**
 * Hello world!
 *
 */
public class Test2CreateDeleteFile  extends TestCase{

    public void testCreateDeleteFile() {
        try {
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            WFS worphFileSytem = new WFS(test,WFS.OpenMode.ForceCreate);
            int fileid1 = worphFileSytem.createFile();
            int fileid2 = worphFileSytem.createFile();
            int fileid3 = worphFileSytem.createFile();
            worphFileSytem.deleteFile(fileid2);
            int fileid4 = worphFileSytem.createFile();
            int fileid5 = worphFileSytem.createFile();
            int fileid6 = worphFileSytem.createFile();      
            worphFileSytem.deleteFile(fileid1);    
            worphFileSytem.deleteFile(fileid5);
            worphFileSytem.deleteFile(fileid6);
            int fileid7 = worphFileSytem.createFile();  
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFS(test);
            worphFileSytem.close();
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(Test2CreateDeleteFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
