package jp.ac.jaist.nguyenlab.extractor;

import edu.washington.cs.knowitall.commonlib.Range;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;

/**
 * Created by chientran on 2017/01/16.
 */
public class ChunkedExtraction {
    private ChunkedSentence sentence;
    private String relation;
    private Range range;

    public ChunkedExtraction(ChunkedSentence sentence, String relation, Range range){
        this.sentence = sentence;
        this.relation = relation;
        this.range = range;
    }

    @Override
    public String toString() {
        return this.relation;
    }

    public Range getRange(){
        return this.range;
    }

    public ChunkedSentence getSentence(){
        return sentence;
    }
}
