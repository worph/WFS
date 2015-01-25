package net.worph.testvfs.nio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.worph.filesytem.IOAbstractionLayer.BytePack;
import net.worph.filesytem.IOAbstractionLayer.memoryMappedImpl.MemoryMapedFile;
import net.worph.filesytem.nio.WFSNIOFileSystemProvider;

/**
 * Hello world!
 *
 */
public class TestReadWriteFile  extends TestCase{
    
    private static String testStr = "Hello World :3";
    
    public void testMain() {
        try {
            WFSNIOFileSystemProvider wfsnioFileSystemProvider = new WFSNIOFileSystemProvider();
            Map<String,Object> env = new HashMap<>();
            File file = new File("./vfsbinaryfile");
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 50000);
            env.put("bytepack", test);
            FileSystem fileSystem = wfsnioFileSystemProvider.newFileSystem(URI.create("wfs:testWFS"),env);
            Path root = fileSystem.getPath("/");
            Path file1 = root.resolve("file.txt");
            
            //create delete
            Files.createFile(file1);
            
            //open close / read write
            OutputStream newOutputStream = Files.newOutputStream(file1, StandardOpenOption.WRITE);
            PrintStream printStream = new PrintStream(newOutputStream);
            printStream.println(testStr);
            printStream.close();
            
            InputStream newInputStream = Files.newInputStream(file1, StandardOpenOption.READ);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(newInputStream));
            String readLine = bufferedReader.readLine();
            bufferedReader.close();
            
            if(!testStr.equals(readLine)){
                throw new IOException("invalide read : "+readLine);
            }            
            //rename copy
        } catch (IOException ex) {
            assertFalse(true);
            Logger.getLogger(TestReadWriteFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
