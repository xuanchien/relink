package jp.ac.jaist.nguyenlab.nlp;

/**
 * Created by chientran on 2017/01/04.
 */
public interface SentenceChunker {
    public ChunkedSentence chunkSentence(String sent);
}
