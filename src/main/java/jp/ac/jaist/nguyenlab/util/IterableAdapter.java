package jp.ac.jaist.nguyenlab.util;

import java.util.Iterator;

/**
 * Created by chientran on 2017/01/06.
 */
public class IterableAdapter<T> implements Iterable<T>{
    private Iterator<T> iter;

    public IterableAdapter(Iterator<T> iter){
        this.iter = iter;
    }
    public Iterator<T> iterator() {
        return this.iter;
    }
}
