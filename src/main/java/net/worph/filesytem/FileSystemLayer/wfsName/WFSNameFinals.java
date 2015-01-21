package net.worph.filesytem.FileSystemLayer.wfsName;

/**
 *
 * @author Worph
 */
public interface WFSNameFinals {
    /* 
     * protocol define 
     */
    //c = constant
    
    public static final int c_nameIndexFileId = 1;//fileidc_FileIdNotInNameIndex
    public static final int c_nameIndexFileIdNotInNameIndex = Integer.MAX_VALUE;//fileid
    public static final int c_nameIndexEntryNotAllocated = Integer.MAX_VALUE;//fileid
    
    //byte
    public static final byte c_nameIndexEraseValue = (byte) 0xFF;//name index id
    public static final byte c_nameIndexEraseValueEnd = (byte) 0xFE;//name index id
}
