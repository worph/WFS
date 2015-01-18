package net.worph.filesytem.FileSystemLayer.wfsBasic;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFinals;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFragment;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFragmentPack;
import net.worph.filesytem.IOAbstractionLayer.BytePack;

/**
 *
 * @author Worph
 */
public class WFSWriterReader implements WFSFinals {

    private final BytePack storage;
    private final WFSFragmentsTable fragments;
    private final HashMap<Integer, Long> fileCurrsor = new HashMap<>();//fileId,cursor

    public WFSWriterReader(BytePack storage, WFSFragmentsTable fragments) {
        this.storage = storage;
        this.fragments = fragments;
    }

    /* open and cursor */
    public void setCurrsor(int fileId, long posistion) {
        fileCurrsor.put(fileId, posistion);
    }

    public void open(int fileid) {
        fileCurrsor.put(fileid, 0L);
    }

    public void close(int fileId) {
        fileCurrsor.remove(fileId);
    }

    /* 
    * write operation 
    */
    public void writeInt(int fileid, int integer) throws IOException {
        byte[] buf = new byte[4];
        buf[0] = (byte) (0xff & (integer >> 24));
        buf[1] = (byte) (0xff & (integer >> 16));
        buf[2] = (byte) (0xff & (integer >> 8));
        buf[3] = (byte) (0xff & integer);
        write(fileid, buf);
    }
    
    public int write(int fileid, byte[] bytes, int offset, int lentgh) throws IOException{
        int start =  fileCurrsor.get(fileid).intValue();
        WFSFragmentPack pack = fragments.getFragmentPack(fileid);
        if (pack == null) {
            throw new FileNotFoundException();
        }
        int totalPreviousFragmentSize = 0;
        int sizeToWriteInThisFragment;
        int lenghtRemaining = lentgh;
        WFSFragment frag = pack.first();//index 0
        do {
            if (start < (totalPreviousFragmentSize + (frag.getStop() - frag.getStart()))) {//start is in this fragment
                // 1 - convert this fragment start offset in storage offset
                int fragOffset = start - totalPreviousFragmentSize;
                // 2 - convert this fragment offset in storage offset
                int storageOffset = frag.getStart() + fragOffset;
                // 3 - compute the effective length to read in this fragment
                if (storageOffset + lenghtRemaining > frag.getStop()) {//lenghtRemaining to write overfow this fragment
                    sizeToWriteInThisFragment = frag.getStop() - storageOffset;
                } else {
                    sizeToWriteInThisFragment = lenghtRemaining;
                }
                // 4 - read the data
                
                internalWrite(storageOffset, bytes, lentgh - lenghtRemaining + offset, sizeToWriteInThisFragment);
                lenghtRemaining -= sizeToWriteInThisFragment;
                start += sizeToWriteInThisFragment;

                if (lenghtRemaining == 0) {
                    setCurrsor(fileid, fileCurrsor.get(fileid) + lentgh);
                    return lentgh;
                }

                //remaining byte to write so we check if there is another fragment
                if (pack.higher(frag)==null) {
                    // at this point we reached the end of the file so we must extends (alocate new space and retry)
                    fragments.allocateAppendSpace(fileid, lentgh - sizeToWriteInThisFragment);
                    //here we have either frag.stop increase either a new fragment or both so we retry this frag (no next())
                } else {
                    totalPreviousFragmentSize += frag.getStop() - frag.getStart();
                    frag = pack.higher(frag);
                }
            } else {
                //remaining byte to write so we check if there is another fragment
                if (pack.higher(frag)==null) {
                    // at this point we reached the end of the file so we must extends (alocate new space and retry)
                    fragments.allocateAppendSpace(fileid, lentgh);
                    //here we have either frag.stop increase either a new fragment or both so we retry this frag (no next())
                } else {
                    totalPreviousFragmentSize += frag.getStop() - frag.getStart();
                    frag = pack.higher(frag);
                }
            }
        } while (pack.higher(frag)!=null || lenghtRemaining != 0);
        throw new EOFException();
    }
    
    public void internalWrite(long start, byte[] data, int offset, int lenght) throws IOException{
        ByteBuffer tmp = ByteBuffer.wrap(data);
        tmp.position(offset);
        tmp.limit(offset+lenght);
        storage.write(tmp, start);
    }

    public int write(int fileid, byte[] bytes) throws IOException {
        return write(fileid, bytes, 0, bytes.length);
    }

    public int readInt(int fileid) throws IOException {
        byte[] read = read(fileid, 4);
        return (((read[0] & 0xff) << 24) | ((read[1] & 0xff) << 16) | ((read[2] & 0xff) << 8) | (read[3] & 0xff));
    }
    
    /*
    * read operation 
    */
    
    public int read(int fileid, byte[] retData, int offset, int lentgh) throws IOException {
        int start = fileCurrsor.get(fileid).intValue();
        WFSFragmentPack pack = fragments.getFragmentPack(fileid);
        if (pack == null) {
            throw new FileNotFoundException();
        }
        int size = lentgh;
        int totalPreviousFragmentSize = 0;
        int sizeToReadInThisFragment;
        int lenghtRemaining = size;
        int sizeRead = 0;
        WFSFragment frag = pack.first();//index 0
        do {
            if (start < (totalPreviousFragmentSize + (frag.getStop() - frag.getStart()))) {//start is in this fragment
                // 1 - convert this fragment start offset in storage offset
                int fragOffset = start - totalPreviousFragmentSize;
                // 2 - convert this fragment offset in storage offset
                int storageOffset = frag.getStart() + fragOffset;
                // 3 - compute the effective length to read in this fragment
                if (storageOffset + lenghtRemaining > frag.getStop()) {//lenghtRemaining to write overfow this fragment
                    sizeToReadInThisFragment = frag.getStop() - storageOffset;//TODO - 1??
                } else {
                    sizeToReadInThisFragment = lenghtRemaining;
                }
                // 4 - we read the data
                byte[] tmp = internalRead(storageOffset, sizeToReadInThisFragment);
                System.arraycopy(tmp, offset, retData, size - lenghtRemaining, tmp.length);
                sizeRead+=tmp.length;
                lenghtRemaining -= sizeToReadInThisFragment;
                start += sizeToReadInThisFragment;

                if (lenghtRemaining == 0) {
                    setCurrsor(fileid, fileCurrsor.get(fileid) + sizeRead);
                    return sizeRead;
                }

                //remaining byte to read so we check if there is another fragment
                if (pack.higher(frag)==null) {
                    setCurrsor(fileid, fileCurrsor.get(fileid) + sizeRead);
                    return sizeRead;
                } else {
                    totalPreviousFragmentSize += frag.getStop() - frag.getStart();
                    frag = pack.higher(frag);
                }
            } else {
                if (pack.higher(frag)==null) {
                    throw new EOFException();
                } else {
                    totalPreviousFragmentSize += frag.getStop() - frag.getStart();
                    frag = pack.higher(frag);
                }
            }
        } while (pack.higher(frag)!=null || lenghtRemaining != 0);
        throw new EOFException();
    }
    
    private byte[] internalRead(long start, int n) throws IOException {
        if(start > Integer.MAX_VALUE) {
            System.out.println("warning");
            throw new IOException();
        }
        ByteBuffer tmp = ByteBuffer.allocate(n);
        storage.read(tmp,start);
        return tmp.array();
    }

    public int read(int fileid, byte[] retData) throws IOException {
        return read(fileid, retData, 0, retData.length);
    }

    public byte[] read(int fileid, int size) throws IOException {
        byte[] ret = new byte[size];
        read(fileid, ret);
        return ret;
    }

    public long getSize(int fileId) {
        return fragments.getSize(fileId);
    }

}
