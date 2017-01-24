package jp.ac.jaist.nguyenlab.extractor;

import edu.washington.cs.knowitall.commonlib.Range;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;

import java.awt.font.NumericShaper;

/**
 * Created by chientran on 2017/01/16.
 */
public class ChunkedArgumentExtraction {
    private ChunkedSentence sentence;
    private String argument;
    private Range range;

    public ChunkedArgumentExtraction(ChunkedSentence sentence, String argument, Range range){
        this.sentence = sentence;
        this.argument = argument;
        this.range = range;
    }

    @Override
    public String toString() {
        return this.argument;
    }

    public Range getRange(){
        return range;
    }
}
