package jp.ac.jaist.nguyenlab.nlp;

import edu.washington.cs.knowitall.commonlib.Range;
import jp.ac.jaist.nguyenlab.sequence.LayeredSequence;

import java.util.ArrayList;

/**
 * Created by chientran on 2017/01/04.
 */
public class ChunkedSentence extends LayeredSequence{
    public static final String TOKEN_LAYER = "tok";
    public static final String POS_LAYER = "pos";
    public static final String CHUNK_LAYER = "chunk";
    public static final String GROUP_LAYER = "group";
    private ArrayList<Range> ranges;

    public ChunkedSentence(String[] tokens, String[] posTags, String[] npChunkTags){
        super(tokens.length);

        addLayer(TOKEN_LAYER, tokens);
        addLayer(POS_LAYER, posTags);
        addLayer(CHUNK_LAYER, npChunkTags);
        String[] groupTags = new String[tokens.length];
        ranges = new ArrayList<Range>(tokens.length);
        for (int i=0; i<tokens.length; i++){
            groupTags[i] = "O";
            ranges.add(new Range(i, 1));
        }
        addLayer(GROUP_LAYER, groupTags);
    }

    public void mergeTokens(int start, int end, String groupName) {
        super.mergeTokens(TOKEN_LAYER, start, end);
        super.mergeTokens(CHUNK_LAYER, start, end);
        super.mergeTokens(POS_LAYER, start, end);
        super.mergeTokens(GROUP_LAYER, start, end, groupName);
        int rangeStart = ranges.get(start).getStart();
        int rangeEnd = ranges.get(end-1).getEnd();
        Range newRange = new Range(rangeStart, rangeEnd - rangeStart);
        int i=start+1;
        while (i < end){
            ranges.remove(start+1);
            i++;
        }
        ranges.set(start, newRange);
    }

    public Range getRangeAt(int index){
        return ranges.get(index);
    }

    @Override
    public String toString() {
        String output = "";
        for (int i=0; i<getLength(); i++){
            String groupName = getLayerValue(GROUP_LAYER, i);
            String token = getLayerValue(TOKEN_LAYER, i);
            String pos = getLayerValue(POS_LAYER, i);
            String chunk = getLayerValue(CHUNK_LAYER, i);

            output += String.format(" [%s %s/%s] ", groupName, token, pos);
        }

        return output;
    }
}
