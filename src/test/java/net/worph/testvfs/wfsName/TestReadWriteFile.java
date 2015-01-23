package net.worph.testvfs.wfsName;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
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
public class TestReadWriteFile extends TestCase{

    //TODO file system extensible avec limite
    
    public void testMain() {
        try {
            URI uri = new URI("file:///file.txt");
            String testStr = "Hello File :3";
            //write
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            WFSName worphFileSytem = new WFSName(test, WFS.OpenMode.ForceCreate);
            worphFileSytem.create(uri);
            FileChannel fileChannel = worphFileSytem.newFileChannel(uri, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
            OutputStream outputStream = Channels.newOutputStream(fileChannel);
            PrintStream printStream = new PrintStream(outputStream);
            printStream.println(testStr);
            fileChannel.close();
            worphFileSytem.close();
            
            //read
            file = new File("./vfsbinaryfile");
            test = new MemoryMapedFile(file.toURI(), 50000);
            worphFileSytem = new WFSName(test);
            fileChannel = worphFileSytem.newFileChannel(uri, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ));
            InputStream inputStream = Channels.newInputStream(fileChannel);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String readLine = bufferedReader.readLine();
            fileChannel.close();
            worphFileSytem.close();
            if(!testStr.equals(readLine)){
                throw new IOException("invalide read : "+readLine);
            }
        } catch (URISyntaxException | IOException ex) {
            assertFalse(true);
            Logger.getLogger(TestReadWriteFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
