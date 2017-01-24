package jp.ac.jaist.nguyenlab.extractor;

import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;
import jp.ac.jaist.nguyenlab.sequence.RegexPattern;

import java.util.regex.Matcher;

/**
 * Created by chientran on 2017/01/05.
 */
public class TokenMerger {
    public static void findAndMerge(ChunkedSentence sentence, String patternString, String groupName){
        RegexPattern pattern = new RegexPattern(patternString);
        Matcher m = pattern.matcher(sentence);

        int relativeIndex = 0;
        while (m.find()){
            int start = m.start();
            int end = m.end();
            sentence.mergeTokens(start-relativeIndex, end-relativeIndex, groupName);
            relativeIndex += end - start - 1;
        }
    }
}
