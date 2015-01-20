package net.worph.testvfs.wfsBasic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
public class Test3FileReadWrite1Byte extends TestCase {
    
    public static byte[] data = {(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD,(byte)0xEE,(byte)0xFF};

    public void testFileReadWrite1Byte() {
        try {
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            WFS worphFileSytem = new WFS(test,WFS.OpenMode.ForceCreate);
            int fileid1 = worphFileSytem.createFile();
            FileChannel fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
            OutputStream outputStream = Channels.newOutputStream(fileChannel);
            outputStream.write(0xDC);
            fileChannel.close();
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFS(test);
            fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ));
            InputStream inputStream = Channels.newInputStream(fileChannel);
            int read = inputStream.read();
            fileChannel.close();
            worphFileSytem.close();
            if((byte)read!=(byte)0xDC){
                throw new IOException("invalide read");
            }
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(Test3FileReadWrite1Byte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
