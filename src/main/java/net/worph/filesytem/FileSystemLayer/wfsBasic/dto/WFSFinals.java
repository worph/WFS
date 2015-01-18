package net.worph.filesytem.FileSystemLayer.wfsBasic.dto;

/**
 *
 * @author Worph
 */
public interface WFSFinals {
    /* 
     * protocol define 
     */
    //c = constant

    public static final int c_tableEndFragment = Integer.MAX_VALUE;
    public static final int c_tableFileIdInFragment = Integer.MAX_VALUE - 1;
    
    public static final int c_fileFirstFragmentNumber = 0;
    public static final int c_fileNotAllocated = 0;
    public static final int c_fileFirstFileId = 1;//name index id
    
    public static final int c_storageFirstOffset = 0;//byte
           
    //misc
    public static final int c_voidValue = 0xABBAACDC;
    public static final int c_integerSize = 4;//byte

}
