package net.worph.filesytem.FileSystemLayer.wfsBasic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFinals;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFragment;
import net.worph.filesytem.FileSystemLayer.wfsBasic.dto.WFSFragmentPack;
import net.worph.filesytem.IOAbstractionLayer.BytePack;

/**
 *
 * @author Worph
 */
public class WFSFragmentsTable implements WFSFinals {

    /* 
     * strorage abstraction 
     */
    private final BytePack storage;
    /* 
     * file system parameters 
     */
    //p parameter
    private final int p_TableEntryAllocationPacket = 5;//entry
    private final int p_freeSpaceAfterFile = 8;//byte
    
    
    ByteBuffer cacheInt = ByteBuffer.allocate(Integer.BYTES);
    
    /*
     * table entry 
     */
    private final ArrayList<WFSFragment> table = new ArrayList<>();//represent table entry as ordoned in the table
    /* represent files and teire fragment (no table fragment) */
    private final HashMap<Integer, WFSFragmentPack> fragmentMap = new HashMap<>();//groupe fragment by file id
    /* all fragments (file fragment + table fragment)*/
    private final TreeSet<WFSFragment> fragmentsList = new TreeSet<>(new WFSFragment.WorphFileSytemFragmentComparator());//represente fragment as ordoned in the storage (by start)

    public WFSFragmentsTable(BytePack storage) {
        this.storage = storage;
    }

    /*
     * 
     * BODY
     * 
     */

    public WFSFragmentPack getFragmentPack(int fileId) throws FileNotFoundException {
        WFSFragmentPack pack = fragmentMap.get(fileId);
        if (pack == null) {
            throw new FileNotFoundException();
        }
        return pack;
    }

    public void writeEntry(int i, int fileId, int fragNum, int start, int stop) throws IOException {
        if (start > stop || start < 0 || stop < 0) {
            throw new IOException("invalid argument");
        }
        WFSFragment get = table.get(i);
        get.setFileid(fileId);
        get.setFragmentNumber(fragNum);
        get.setStart(start);
        get.setStop(stop);
        table_writeEntryToStorage(i);
    }

    public void readTable() throws IOException {
        readTableFragmentRec(0);
    }

    public void deallocateEntry(int fileid) throws IOException {
        int i = 0;
        for (WFSFragment tableEntry : table) {
            if (tableEntry.getFileid() == fileid) {
                tableEntry.setFileid(c_fileNotAllocated);
                tableEntry.setFragmentNumber(c_voidValue);
                tableEntry.setStart(c_voidValue);
                tableEntry.setStop(c_voidValue);
                table_writeEntryToStorage(i);
                WFSFragmentPack toRemove = fragmentMap.get(fileid);
                fragmentsList.removeAll(toRemove);
                fragmentMap.remove(fileid);
                return;
            }
            i++;
        }
        throw new IOException("File not exist");
    }

    /* 
     * fragment pure function 
     */

    /* update an entry to cache (fragmentMap,fragmentsList) */
    private WFSFragment frag_addNewEntryToFragmentToCache(WFSFragment entry) throws IOException {
        if (entry.getStart() > entry.getStop()) {
            throw new IOException("invalid argument");
        }
        if (entry.getFileid() != c_fileNotAllocated) {
            if (!fragmentMap.containsKey(entry.getFileid())) {
                fragmentMap.put(entry.getFileid(), new WFSFragmentPack(new WFSFragment.WorphFileSytemFragmentComparator()));
            }
            WFSFragmentPack fragments = fragmentMap.get(entry.getFileid());
            WFSFragment frag = new WFSFragment(entry.getFileid(), fragments.size(), entry.getStart(), entry.getStop());
            fragments.add(frag);
            fragmentsList.add(frag);
            return frag;
        }
        return null;
    }

    private void frag_addTableFragment(int nextFragment, int start, int stop) throws IOException {
        if (start > stop || start < 0 || stop < 0 || start==stop) {
            throw new IOException("invalid argument");
        }            
        WFSFragment frag = new WFSFragment(c_tableFileIdInFragment, nextFragment, start, stop);//in case of table entry we use next fragment beware
        fragmentsList.add(frag);
    }

    /* 
     * fragment and table function 
     */
    public int allocateNewTableFragment() throws IOException {
        int tableSize = p_TableEntryAllocationPacket;//+1 ==> for the nameindex
        int start = findAvailableSpace(0);
        storage.position(start);
        writeInt(tableSize);//<TableSize>

        int lenght = c_integerSize + (tableSize * WFSFragment.getEntrySize()) + c_integerSize;
        int stop = start + lenght;

        for (int i = 0; i < tableSize; i++) {
            writeInt(c_fileNotAllocated);//<FileId> //1 because the file start at 1 and name index = 1
            writeInt(c_voidValue);//<Fragment>
            writeInt(c_voidValue);//<data start>
            writeInt(c_voidValue);//<data stop>
            table.add(new WFSFragment(c_fileNotAllocated, c_voidValue, c_voidValue, c_voidValue));
        }

        writeInt(c_tableEndFragment);//<data start of next fragment> //0 because new fragment
        frag_addTableFragment(c_tableEndFragment, start, stop);
        //connect table last fragment to previous fragment
        if (start != c_storageFirstOffset) {//in this case its the first fragment table
            table_connectTableFragmentToCurrentLastTableFragment(start);
        }
        return lenght;
    }
    
    private void readTableFragmentRec(int offset) throws IOException {
        if (offset == c_tableEndFragment) {
            return;
        }
        int tableLenght = 0;
        storage.position(offset);
        int tableSize = readInt();
        tableLenght += c_integerSize;
        for (int i = 0; i < tableSize; i++) {
            int fileId = readInt();
            tableLenght += c_integerSize;
            int fragNum = readInt();
            tableLenght += c_integerSize;
            int start = readInt();
            tableLenght += c_integerSize;
            int stop = readInt();
            tableLenght += c_integerSize;
            WFSFragment newTableEntry = new WFSFragment(fileId, fragNum, start, stop);
            table.add(newTableEntry);
            frag_addNewEntryToFragmentToCache(newTableEntry);
        }

        int tableNextFragment = readInt();
        tableLenght += c_integerSize;

        frag_addTableFragment(tableNextFragment, offset, offset + tableLenght);
        if (tableNextFragment != c_tableEndFragment) {
            readTableFragmentRec(tableNextFragment);
        }
    }

    /*
     * table pure function
     */
    private void table_writeEntryToStorage(int entry) throws IOException {
        int tableNextFragment = c_storageFirstOffset;
        int cursor;
        int tableIndex = 0;
        do {
            cursor = tableNextFragment;
            storage.position(cursor);
            int tableSize = readInt();
            if ((tableIndex + tableSize) <= entry) {
                //skip fragment
                cursor += c_integerSize + tableSize * WFSFragment.getEntrySize();
                storage.position(cursor);
                tableNextFragment = readInt();
                tableIndex += tableSize;
            } else {
                int offset = entry - tableIndex;// entry offset in the current table
                //write entry in this table
                cursor += c_integerSize + offset * WFSFragment.getEntrySize();
                storage.position(cursor);
                WFSFragment tabEntry = table.get(entry);
                writeInt(tabEntry.getFileid());
                writeInt(tabEntry.getFragmentNumber());
                writeInt(tabEntry.getStart());
                writeInt(tabEntry.getStop());
                break;
            }
        } while (tableNextFragment != c_tableEndFragment);
    }

    private void table_connectTableFragmentToCurrentLastTableFragment(int currentFragment) throws IOException {
        storage.position(c_storageFirstOffset);
        int tableNextFragment = c_storageFirstOffset;
        int cursor;
        do {
            storage.position(tableNextFragment);
            cursor = tableNextFragment;
            int tableSize = readInt();
            cursor += c_integerSize + tableSize * WFSFragment.getEntrySize();
            storage.position(cursor);
            tableNextFragment = readInt();
        } while (tableNextFragment != c_tableEndFragment);
        //here the cursor is a the right position
        storage.position(cursor);
        writeInt(currentFragment);
        //TODO update table fragment
    }

    private int findAvailableFileId() {
        ArrayList<Integer> fileIds = new ArrayList<>(fragmentMap.keySet());
        Collections.sort(fileIds);//TODO optimisation TreeSet
        if (fileIds.isEmpty()) {
            return c_fileFirstFileId;
        }
        int prev = fileIds.get(0);
        for (int id : fileIds) {
            if ((prev + 1) < id) {
                return prev + 1;
            }
            prev = id;
        }
        return fileIds.get(fileIds.size() - 1) + 1;
    }

    /* return startpoint */
    private int findAvailableSpace(int minSize) throws IOException {
        if (minSize < 0) {
            throw new IOException("invalid argument");
        }
        if (fragmentsList.isEmpty()) {
            return c_storageFirstOffset;
        }
        for (WFSFragment frag : fragmentsList) {
            WFSFragment next = fragmentsList.higher(frag);
            if (next != null) {
                if ((next.getStart() - frag.getStop()) > (2 * p_freeSpaceAfterFile + minSize)) {
                    return frag.getStop() + p_freeSpaceAfterFile;
                }
            } else {//if next == null ==> frag ==> last fragment
                return frag.getStop() + p_freeSpaceAfterFile;
            }
        }
        throw new IOException("Unkown");
    }

    /* add new a new entry in table return file ID*/
    public int allocateNewFileId() throws IOException {
        // 1 - find a place
        int i = 0;
        for (WFSFragment entry : table) {
            if (entry.getFileid() == c_fileNotAllocated) {
                // 1 - new entry (table)
                int fileid = findAvailableFileId();
                int start = findAvailableSpace(0);
                writeEntry(i, fileid, c_fileFirstFragmentNumber, start, start);

                // 2 - new fragment (fragmentMap,fragmentsList)
                frag_addNewEntryToFragmentToCache(entry);
                return fileid;
            }
            i++;
        }
        // 1.1 - if no place allocate place //TODO optimisation
        allocateNewTableFragment();
        return allocateNewFileId();
    }
       
    private int readInt() throws IOException{
        cacheInt.position(0);
        storage.read(cacheInt);
        return cacheInt.getInt(0);
    }
    
    private void writeInt(int intToWrite) throws IOException{
        cacheInt.putInt(0,intToWrite);
        cacheInt.position(0);
        storage.write(cacheInt);
    }

    /*
     * 
     * accesors
     * 
     */
    private boolean isFragmentListEmpty() {
        return fragmentsList.isEmpty();
    }

    private WFSFragment getLast() {
        return fragmentsList.last();
    }

    public void allocateAppendSpace(int fileId, int size) throws IOException {
        if (size < 0) {
            throw new IOException("invalid argument");
        }
        // 1 - check if there is free space after last fragment
        // 2 - if there is not complete the space and create a new fragment after the en of the file + marge
        WFSFragmentPack fragmentPack = getFragmentPack(fileId);
        WFSFragment lastFragment = fragmentPack.last();
        // - 3 get fragment next to last fragment first fragement after lastFragment.end ordoner les fragment
        WFSFragment ceiling = fragmentsList.higher(lastFragment);
        if (ceiling == null) {// this is the last fragment
            lastFragment.setStop(lastFragment.getStop() + size);
            int entryIndex = findEntry(lastFragment.getFileid(), lastFragment.getFragmentNumber());
            writeEntry(entryIndex, lastFragment.getFileid(), lastFragment.getFragmentNumber(), lastFragment.getStart(), lastFragment.getStop());
        } else {
            if (ceiling.getStart() - lastFragment.getStop() >= size) {
                // the is space so we extends the last fragment
                lastFragment.setStop(lastFragment.getStop() + size);
                int entryIndex = findEntry(lastFragment.getFileid(), lastFragment.getFragmentNumber());
                writeEntry(entryIndex, lastFragment.getFileid(), lastFragment.getFragmentNumber(), lastFragment.getStart(), lastFragment.getStop());
            } else {
                // there is no suficient space so we extends the las array and allocate a new one
                int firstLenght = (ceiling.getStart() - lastFragment.getStop());
                lastFragment.setStop(lastFragment.getStop() + firstLenght);
                int entryIndex = findEntry(lastFragment.getFileid(), lastFragment.getFragmentNumber());
                writeEntry(entryIndex, lastFragment.getFileid(), lastFragment.getFragmentNumber(), lastFragment.getStart(), lastFragment.getStop());
                int lastLenght = size - firstLenght;
                //allocate new file fragment
                allocateNewFileFragment(fileId, lastLenght);
            }
        }

    }

    public int findEntry(int fileId, int fragmentNumber) throws IOException {
        if (fragmentNumber < 0) {
            throw new IOException("invalid argument");
        }
        int i = 0;
        for (WFSFragment entry : table) {
            if (entry.getFileid() == fileId && entry.getFragmentNumber() == fragmentNumber) {
                return i;
            }
            i++;
        }
        return i;
    }

    public void allocateNewFileFragment(int Fileid, int size) throws IOException {
        if (size <= 0) {
            throw new IOException("invalid argument");
        }
        WFSFragmentPack pack = fragmentMap.get(Fileid);
        // 1 - find a place
        int i = 0;
        for (WFSFragment entry : table) {
            if (entry.getFileid() == c_fileNotAllocated) {
                // 1 - new entry (table)
                int fileid = Fileid;
                int start = findAvailableSpace(size);
                writeEntry(i, fileid, pack.size(), start, start + size);//pack.size() ==> next fragment number

                // 2 - new fragment (fragmentMap,fragmentsList)
                frag_addNewEntryToFragmentToCache(entry);
                return;
            }
            i++;
        }
        // 1.1 - if no place allocate place //TODO optimisation
        allocateNewTableFragment();
        allocateNewFileFragment(Fileid, size);
    }

    long getSize(int fileId) {//TODO cache size ?
        WFSFragmentPack get = fragmentMap.get(fileId);
        long size = 0;
        for (WFSFragment wFSFragment : get) {
            size+=wFSFragment.getStop()-wFSFragment.getStart();
        }
        return size;
    }
}
