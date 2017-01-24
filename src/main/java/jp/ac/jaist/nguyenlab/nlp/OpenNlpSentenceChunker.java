package jp.ac.jaist.nguyenlab.nlp;

/*
Code inherited from https://github.com/knowitall/reverb/blob/master/core/src/main/java/edu/washington/cs/knowitall/nlp/OpenNlpSentenceChunker.java
 */
import edu.washington.cs.knowitall.commonlib.Range;
import jp.ac.jaist.nguyenlab.sequence.RegexPattern;
import jp.ac.jaist.nguyenlab.util.Utils;
import opennlp.tools.chunker.Chunker;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chientran on 2017/01/04.
 */
public class OpenNlpSentenceChunker implements SentenceChunker {
    private Chunker chunker;
    private POSTagger posTagger;
    private Tokenizer tokenizer;

    public OpenNlpSentenceChunker() throws IOException{
        this.chunker = Utils.getDefaultChunker();
        this.posTagger = Utils.getDefaultPosTagger();
        this.tokenizer = Utils.getDefaultTokenizer();
    }

    public ChunkedSentence chunkSentence(String sent) throws ChunkerException {
        ArrayList<Range> ranges;
        String[] tokens, posTags, npChunkTags;
        try {
            String[] tokensX = tokenizer.tokenize(sent);
            Span[] offsets = tokenizer.tokenizePos(sent);
            ranges = new ArrayList<Range>(offsets.length);
            ArrayList<String> tokenList = new ArrayList<String>(offsets.length);
            for (Span span : offsets) {
                ranges.add(Range.fromInterval(span.getStart(), span.getEnd()));
                String token = sent.substring(span.getStart(), span.getEnd());
                tokenList.add(token);
            }

            tokens = tokenList.toArray(new String[] {});
            posTags = posTagger.tag(tokens);
            npChunkTags = chunker.chunk(tokens, posTags);

        } catch (NullPointerException e) {
            throw new ChunkerException("OpenNLP threw NPE on '" + sent + "'", e);
        }

        ChunkedSentence chunkedSentence = new ChunkedSentence(tokens, posTags, npChunkTags);

        return chunkedSentence;
    }

    public static void main(String[] args) throws Exception{
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence chunkedSentence = chunker.chunkSentence("I want to go to school");
        System.out.println(chunkedSentence.toString(ChunkedSentence.CHUNK_LAYER));
        System.out.println(chunkedSentence.toString(ChunkedSentence.TOKEN_LAYER));
        System.out.println(chunkedSentence.toString(ChunkedSentence.POS_LAYER));

        RegexPattern pattern = new RegexPattern("TO_pos? TO_pos?");
        Pattern pattern1 = pattern.getEncodedPattern();
        Matcher m = pattern.matcher(chunkedSentence);

        while(m.find()){
            System.out.println("Match position: " +  m.start() + " -> " + m.end());
        }
    }
}
