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
public class Test1CreateRead  extends TestCase{

    public void testCreateAndReadFileSystem() {
        try {
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            WFS worphFileSytem = new WFS(test, WFS.OpenMode.ForceCreate);
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFS(test);
            worphFileSytem.close();
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(Test1CreateRead.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
