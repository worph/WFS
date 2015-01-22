package net.worph.testvfs.wfsBasic;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
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
public class Test3FileWrite extends TestCase {
    
    public static byte[] data = {(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD,(byte)0xEE,(byte)0xFF};
    public void testMain() {
        try {
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            WFS worphFileSytem = new WFS(test,WFS.OpenMode.ForceCreate);
            int fileid1 = worphFileSytem.createFile();
            FileChannel fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
            OutputStream outputStream = Channels.newOutputStream(fileChannel);
            outputStream.write(0x0C);
            outputStream.write(0x0E);
            outputStream.write(0x00);
            outputStream.write(0x0F);
            outputStream.write(0x0D);
            outputStream.write(data);
            fileChannel.close();
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFS(test);
            worphFileSytem.close();
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(Test3FileWrite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
