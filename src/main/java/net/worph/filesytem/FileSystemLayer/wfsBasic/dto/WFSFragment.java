package net.worph.filesytem.FileSystemLayer.wfsBasic.dto;

import java.util.Comparator;

/**
 *
 * @author Worph
 */
public class WFSFragment implements WFSFinals {
    //TODO defragmenter

    private int fileid;//TODO move this field in WFSFragmentPack
    private int start;
    private int stop;
    private int fragmentNumber;

    public static class WorphFileSytemFragmentComparator implements Comparator<WFSFragment> {

        @Override
        public int compare(WFSFragment a, WFSFragment b) {
            return Integer.compare(a.getStart(), b.getStart());
        }
    }

    public WFSFragment(int fileId, int fragmentNumber, int start, int stop) {
        if((fragmentNumber<0 || fileId<0)
            && !(fileId==c_fileNotAllocated && fragmentNumber==c_voidValue && start==c_voidValue && stop==c_voidValue)//Special case unallocated fragment
                ){
            throw new IllegalArgumentException();
        }
        this.fileid = fileId;
        this.start = start;
        this.stop = stop;
        this.fragmentNumber = fragmentNumber;
    }

    public static int getEntrySize() {
        return c_integerSize * 4;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.fileid;
        hash = 67 * hash + this.start;
        hash = 67 * hash + this.stop;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WFSFragment other = (WFSFragment) obj;
        if (this.fileid != other.fileid) {
            return false;
        }
        if (this.start != other.start) {
            return false;
        }
        if (this.stop != other.stop) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "F{" + "fId=" + fileid + " fNum=" + fragmentNumber + " [" + start + ":" + stop + "]}";
    }
    
    public int getFileid() {
        return fileid;
    }

    public int getStop() {
        return stop;
    }

    public int getFragmentNumber() {
        return fragmentNumber;
    }

    public int getStart() {
        return start;
    }
    
    public void setFileid(int fileid) {
        this.fileid = fileid;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public void setFragmentNumber(int fragmentNumber) {
        this.fragmentNumber = fragmentNumber;
    }
    
}
