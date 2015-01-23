package net.worph.testvfs.wfsName;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;
import net.worph.filesytem.FileSystemLayer.wfsName.WFSName;
import net.worph.filesytem.IOAbstractionLayer.BytePack;
import net.worph.filesytem.IOAbstractionLayer.memoryMappedImpl.MemoryMapedFile;

/**
 * Hello world!
 *
 */
public class TestCreateOpen  extends TestCase{

    public void testMain() {
        try {
            URI uri = new URI("file:///file.txt");
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            WFSName worphFileSytem = new WFSName(test, WFS.OpenMode.ForceCreate);
            worphFileSytem.create(uri);
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFSName(test);
            worphFileSytem.close();
        } catch (URISyntaxException | IOException ex) {
            assertFalse(true);
            Logger.getLogger(TestCreateOpen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
