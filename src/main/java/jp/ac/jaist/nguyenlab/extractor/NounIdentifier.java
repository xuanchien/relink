package jp.ac.jaist.nguyenlab.extractor;

import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;
import jp.ac.jaist.nguyenlab.sequence.RegexPattern;

/**
 * Created by chientran on 2017/01/04.
 */
public class NounIdentifier {
    private static final String NOUN_PATTERN = "B-NP_chunk I-NP_chunk* (POS_pos? I-NP_chunk*)+";

    private static final String EXTENDED_PATTERN_1 = "NP_group (of_tok NP_group)+";
    private static final String EXTENDED_PATTERN_2 = "NP_group IN_pos VP-G_group NP_group";
    private static final String EXTENDED_PATTERN_3 = "NP_group O_group NP_group (?=O_group)";
    private static final String EXTENDED_PATTERN_4 = "NP_group and_tok NP_group (?![VP-A_group])";
    private static final String EXTENDED_PATTERN_5 = "(?<=B-SBAR_chunk) NP_group and_tok NP_group";

    public void identifyNoun(ChunkedSentence sentence){
        TokenMerger.findAndMerge(sentence, NOUN_PATTERN, "NP");
    }

    public void extendNoun(ChunkedSentence sentence){
        TokenMerger.findAndMerge(sentence, EXTENDED_PATTERN_1, "NP");
        TokenMerger.findAndMerge(sentence, EXTENDED_PATTERN_2, "NP");
        //TokenMerger.findAndMerge(sentence, EXTENDED_PATTERN_3, "NP");
        TokenMerger.findAndMerge(sentence, EXTENDED_PATTERN_4, "NP");
        TokenMerger.findAndMerge(sentence, EXTENDED_PATTERN_5, "NP");
    }
}
