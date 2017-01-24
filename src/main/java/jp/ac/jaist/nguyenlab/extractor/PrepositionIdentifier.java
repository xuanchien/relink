package jp.ac.jaist.nguyenlab.extractor;

import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;
import jp.ac.jaist.nguyenlab.sequence.RegexPattern;

/**
 * Created by chientran on 2017/01/05.
 */
public class PrepositionIdentifier {
    private final String PP_PATTERN = "B-PP_chunk I-PP_chunk*";

    public void identifyPreposition(ChunkedSentence sentence){
        TokenMerger.findAndMerge(sentence, PP_PATTERN, "PP");
    }
}
