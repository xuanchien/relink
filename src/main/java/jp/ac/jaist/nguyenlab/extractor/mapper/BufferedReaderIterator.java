package jp.ac.jaist.nguyenlab.extractor.mapper;

import com.google.common.collect.AbstractIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by chientran on 2017/01/04.
 */
public class BufferedReaderIterator extends AbstractIterator<String> {
    private BufferedReader buffered;
    public BufferedReaderIterator(Reader in){
        this.buffered = new BufferedReader(in);
    }
    @Override
    protected String computeNext() {
        try{
            String line = this.buffered.readLine();
            if (line != null){
                return line;
            }else{
                return endOfData();
            }
        }catch(IOException ex){
            return endOfData();
        }
    }
}
