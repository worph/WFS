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
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;
import net.worph.filesytem.FileSystemLayer.wfsName.WFSName;
import net.worph.filesytem.IOAbstractionLayer.BytePack;
import net.worph.filesytem.IOAbstractionLayer.memoryMappedImpl.MemoryMapedFile;
import net.worph.testvfs.Utils;

/**
 * Hello world!
 *
 */
public class TestStress extends TestCase{
    
    public void testA() {
        Random rand = new Random(412);
        try {
            HashMap<String, String> testData = new HashMap<>();
            String testStr = "Hello File :3/";
            //write
            File file = new File("./vfsbinaryfile");
            file.deleteOnExit();
            file.delete();
            file.createNewFile();
            BytePack test = new MemoryMapedFile(file.toURI(), 200*1000*1000);
            WFSName worphFileSytem = new WFSName(test, WFS.OpenMode.ForceCreate);
            
            int stressTestNbFile = 500;
            int stressTestMaxData = 400;

            for (int i = 0; i < stressTestNbFile; i++) {
                URI uri = new URI("file:///file" + i + ".txt");
                System.out.println("Write:"+uri.toString());
                FileChannel fileChannel = worphFileSytem.newFileChannel(uri, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE));
                OutputStream outputStream = Channels.newOutputStream(fileChannel);
                PrintStream printStream = new PrintStream(outputStream);
                String key = testStr + "" + i;
                testData.put(key,Utils.generate(rand.nextInt(stressTestMaxData)));
                printStream.print(testData.get(key));
                fileChannel.close();
            }

            for (int i = 0; i < stressTestNbFile; i++) {
                String key = testStr + "" + i;
                URI uri = new URI("file:///file" + i + ".txt");
                System.out.println("Read:"+uri.toString());
                if(uri.toString().equals("file:///file419.txt")){
                    System.out.println("debug");
                }
                FileChannel fileChannel = worphFileSytem.newFileChannel(uri, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE));
                InputStream inputStream = Channels.newInputStream(fileChannel);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String readLine = bufferedReader.readLine();
                if(readLine==null){
                    readLine="";
                }
                System.out.println(readLine);
                if (!readLine.equals(testData.get(key))) {
                    throw new Error("DATA invalid");
                }
                fileChannel.close();
            }

            worphFileSytem.close();
        } catch (URISyntaxException | IOException ex) {
            assertFalse(true);
            Logger.getLogger(TestStress.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
