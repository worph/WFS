package net.worph.filesytem.FileSystemLayer.wfsBasic.dto;

import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author Worph
 */
public class WFSFragmentPack extends TreeSet<WFSFragment>{

    public WFSFragmentPack(Comparator<? super WFSFragment> comparator) {
        super(comparator);
    }

    @Override
    public boolean add(WFSFragment e) {
        //note :  empty fragment authorized only if its the only fragment in the file (file not empty)
        if(size()>=1 && (e.getStop()-e.getStart())<1){
            throw new IllegalArgumentException();
        }
        return super.add(e);
    }
        
}
