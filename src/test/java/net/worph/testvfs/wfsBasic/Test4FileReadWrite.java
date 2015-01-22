package net.worph.testvfs.wfsBasic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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
public class Test4FileReadWrite  extends TestCase{
    
    public static byte[] data = {(byte)0x11,(byte)0x22,(byte)0x33,(byte)0x44,(byte)0x55,(byte)0x66,(byte)0x77,(byte)0x88,(byte)0x99,(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD,(byte)0xEE,(byte)0xFF,(byte)0x11,(byte)0x22,(byte)0x33,(byte)0x44,(byte)0x55,(byte)0x66,(byte)0x77,(byte)0x88,(byte)0x99,(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD,(byte)0xEE,(byte)0xFF};
    //public static byte[] data2 = {(byte)0xBB,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xFF};
    public static byte[] data2 =  "Hello World :3".getBytes(Charset.forName("UTF-8"));
    
    public void testMain() {
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
            worphFileSytem.deleteFile(fileid5);
            worphFileSytem.deleteFile(fileid6);
            int fileid7 = worphFileSytem.createFile();
            FileChannel fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
            OutputStream outputStream = Channels.newOutputStream(fileChannel);
            FileChannel fileChannel2 = worphFileSytem.newFileChannel(fileid3, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
            OutputStream outputStream2 = Channels.newOutputStream(fileChannel2);
            outputStream2.write(data2);
            outputStream.write(data);
            fileChannel.close();
            fileChannel2.close();
            worphFileSytem.deleteFile(fileid7);
            int fileid8 = worphFileSytem.createFile(); 
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFS(test);
            fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ));
            InputStream inputStream = Channels.newInputStream(fileChannel);
            fileChannel2 = worphFileSytem.newFileChannel(fileid3, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ));
            InputStream inputStream2 = Channels.newInputStream(fileChannel2);
            byte[] read = new byte[data.length];
            byte[] read2 = new byte[data2.length];
            inputStream.read(read);
            inputStream2.read(read2);
            fileChannel.close();
            fileChannel2.close();
            worphFileSytem.close();
            if(!Arrays.equals(data, read) || !"Hello World :3".equals(new String(read2, Charset.forName("UTF-8")))){
                throw new IOException("invalide read");
            }
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(Test4FileReadWrite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
