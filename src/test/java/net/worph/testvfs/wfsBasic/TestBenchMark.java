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
import net.worph.testvfs.Utils;

/**
 * Hello world!
 *
 */
public class TestBenchMark  extends TestCase{
    
    public static byte[] data = {(byte)0x11,(byte)0x22,(byte)0x33,(byte)0x44,(byte)0x55,(byte)0x66,(byte)0x77,(byte)0x88,(byte)0x99,(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD,(byte)0xEE,(byte)0xFF,(byte)0x11,(byte)0x22,(byte)0x33,(byte)0x44,(byte)0x55,(byte)0x66,(byte)0x77,(byte)0x88,(byte)0x99,(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD,(byte)0xEE,(byte)0xFF};
    public static byte[] data2 = {(byte)0xBB,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xEE,(byte)0xFF};

    public void testMain() {
        try {
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            int allocatedLenght = 30000000;
            BytePack test = new MemoryMapedFile(file.toURI(), allocatedLenght);
            WFS worphFileSytem = new WFS(test,WFS.OpenMode.ForceCreate);    
            int size = 3000;
            byte[] testFileData = Utils.generate(size).getBytes();
            long start = System.nanoTime();
            int lenghtWrited = 0;
            int i = 0;
            while(true){
                try{
                    int fileid1 = worphFileSytem.createFile();
                    FileChannel fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
                    OutputStream outputStream = Channels.newOutputStream(fileChannel);
                    outputStream.write(testFileData);
                    lenghtWrited += size;
                    i++;
                }catch(java.nio.BufferOverflowException | java.lang.IllegalArgumentException ex){
                    break;
                }
            }
                        
            long stop = System.nanoTime();
            System.out.println("time eleapsed in mili : "+(stop-start)/1000000.0);
            System.out.println(i+" file : total lenght : "+lenghtWrited);
            System.out.println("file size : "+size);
            System.out.println("allocatedLenght : "+allocatedLenght);
            System.out.println("ratio charge utile : "+((float)lenghtWrited)/allocatedLenght);
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), allocatedLenght);
            worphFileSytem = new WFS(test);
            worphFileSytem.close();
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(TestBenchMark.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
