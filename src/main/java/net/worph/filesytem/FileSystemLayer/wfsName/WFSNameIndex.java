package net.worph.filesytem.FileSystemLayer.wfsName;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import net.worph.filesytem.FileSystemLayer.wfsBasic.WFS;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFinals;

/**
 *
 * @author Worph
 */
class WFSNameIndex implements WFSFinals, WFSNameFinals {

    private static class NameIndexEntry {

        String name;
        int fileId;
        int start;
        int stop;

        public NameIndexEntry(String name, int fileId, int start, int stop) {
            this.name = name;
            this.fileId = fileId;
            this.start = start;//start offset in file
            this.stop = stop;//stop offset in file
        }

        public static class NameIndexEntryComparator implements Comparator<NameIndexEntry> {

            @Override
            public int compare(NameIndexEntry a, NameIndexEntry b) {
                return Integer.compare(a.start, b.start);
            }
        }
    }
    /* 
     * strorage abstraction 
     */
    private final WFS basicFileSystem;
    private final Charset charset = Charset.forName("UTF-8");
    //private WFSFile nameIndexFile;
    private FileChannel nameIndexFileChannel;
    
    /* represent name index */
    private final HashMap<String, Integer> nameIndex = new HashMap<>();
    private final TreeSet<NameIndexEntry> tableList = new TreeSet<>(new NameIndexEntry.NameIndexEntryComparator());
    
    //Buffers
    byte[] byteBuffer = new byte[1000];
    ByteBuffer byteBuffer2 = ByteBuffer.allocate(1000);

    public WFSNameIndex(WFS basicFileSystem) {
        this.basicFileSystem = basicFileSystem;
    }

    /* 
     * name index functions 
     */
    public int getFileId(String name) {
        Integer i = nameIndex.get(name);
        if (i == null) {
            return c_nameIndexFileIdNotInNameIndex;
        }
        return i;
    }

    public void createNameIndex() throws IOException{
        int createFile = basicFileSystem.createFile();
        if(createFile!=c_nameIndexFileId){
            throw new IOException("fileIndex is not the first file of this file system");
        }
        /*nameIndexFile = (WFSFile) basicFileSystem.open(c_nameIndexFileId, WBasicFileSystem.Mode.ReadWrite);
        nameIndexFileChannel = nameIndexFile.getFileChannel();*/
        nameIndexFileChannel = basicFileSystem.newFileChannel(c_nameIndexFileId,EnumSet.<StandardOpenOption>of(StandardOpenOption.READ,StandardOpenOption.WRITE));
    }
    
    public void readNameIndex() throws IOException {
        /*nameIndexFile = (WFSFile) basicFileSystem.open(c_nameIndexFileId, WBasicFileSystem.Mode.ReadWrite);
        nameIndexFileChannel = nameIndexFile.getFileChannel();*/
        nameIndexFileChannel = basicFileSystem.newFileChannel(c_nameIndexFileId,EnumSet.<StandardOpenOption>of(StandardOpenOption.READ,StandardOpenOption.WRITE));
        //InputStream inputStream = nameIndexFile.getInputStream();
        InputStream inputStream = Channels.newInputStream(nameIndexFileChannel);
        DataInputStream inputStreamReader = new DataInputStream(inputStream);
        int i = 0;
        try{
        while(true){
            // 1 - skip erased value
            byte b = (byte) 0xAA;
            while(b!=c_nameIndexEraseValueEnd && b==c_nameIndexEraseValue){
                b = (byte) inputStreamReader.read();
            }
            int start = i;
            int stringLenght = inputStreamReader.readInt();
            i += c_integerSize;
            if(stringLenght!=c_nameIndexEntryNotAllocated){
                if(stringLenght>byteBuffer.length){
                    byteBuffer = new byte[stringLenght];
                }
                i+=inputStreamReader.read(byteBuffer,0,stringLenght);
                String name = new String(byteBuffer, 0, stringLenght, charset);
                int fileId = inputStreamReader.readInt();
                i += c_integerSize;
                nameIndex.put(name, fileId);
                tableList.add(new NameIndexEntry(name, fileId, start, i));
            }
        }
        }catch(EOFException ex){
            
        }
    }
    
    void deleteEntry(String uri) throws IOException{
        // 1 - find NameIndexEntry for this url then erase the infomation and remove from cache
        for (Iterator<NameIndexEntry> it = tableList.iterator(); it.hasNext();) {
            NameIndexEntry nameIndexEntry = it.next();
            if(nameIndexEntry.name.equals(uri)){
                int lenghtToWrite = nameIndexEntry.stop-nameIndexEntry.start;
                if(lenghtToWrite>byteBuffer2.capacity()){
                    byteBuffer2 = ByteBuffer.allocate(lenghtToWrite);
                }
                nameIndexFileChannel.position(nameIndexEntry.start);
                byteBuffer2.position(0);
                byteBuffer2.limit(lenghtToWrite);
                int i = 0;
                while(i<nameIndexEntry.stop-1){
                    byteBuffer2.put(c_nameIndexEraseValue);
                    i++;
                }
                byteBuffer2.put(c_nameIndexEraseValueEnd);
                byteBuffer2.position(0);
                nameIndexFileChannel.write(byteBuffer2);
                nameIndex.remove(uri);
                it.remove();
            }
        }
    }

    void addNewEntry(String uri, int newFileID) throws IOException {
        // 1 - find available space or append to the end and write data and write to cache (1)
        byte[] stringData = uri.getBytes(charset);
        int lenghtToWrite = 2*c_integerSize+stringData.length;
        if(lenghtToWrite>byteBuffer2.capacity()){
            byteBuffer2 = ByteBuffer.allocate(lenghtToWrite);
        }
        byteBuffer2.position(0);
        byteBuffer2.limit(lenghtToWrite);
        Iterator<NameIndexEntry> it = tableList.iterator();
        boolean spaceFound = false;
        if(it.hasNext()){
            NameIndexEntry prev = it.next();
            while(it.hasNext()) {
                NameIndexEntry next = it.next();
                if((next.start-prev.stop)>lenghtToWrite){
                    //we find a space to write an entry
                    int localCursor = prev.stop;
                    nameIndexFileChannel.position(prev.stop);
                    byteBuffer2.putInt(stringData.length);
                    localCursor+=c_integerSize;
                    byteBuffer2.put(stringData, 0, stringData.length);
                    localCursor+=stringData.length;
                    byteBuffer2.putInt(newFileID);
                    localCursor+=c_integerSize;
                    tableList.add(new NameIndexEntry(uri, newFileID, prev.stop, localCursor));
                    //we fill remaining space until next.start
                    byteBuffer2.limit(next.start-prev.stop);
                    while(localCursor<next.start-1){
                        byteBuffer2.put(c_nameIndexEraseValue);
                    }
                    byteBuffer2.put(c_nameIndexEraseValueEnd);
                    byteBuffer2.position(0);
                    nameIndexFileChannel.write(byteBuffer2);
                    spaceFound = true;
                    break;
                }
                prev = next;
            }
        }
        if(!spaceFound){
            // name index vide on append
            int start =(int) nameIndexFileChannel.size();
            nameIndexFileChannel.position(nameIndexFileChannel.size());
            byteBuffer2.putInt(stringData.length);
            byteBuffer2.put(stringData, 0, stringData.length);
            byteBuffer2.putInt(newFileID);
            byteBuffer2.position(0);
            nameIndexFileChannel.write(byteBuffer2);
            tableList.add(new NameIndexEntry(uri, newFileID, start, start+lenghtToWrite));
        }
        
        // 2 - write to cache (2)
        nameIndex.put(uri, newFileID);
    }
}
