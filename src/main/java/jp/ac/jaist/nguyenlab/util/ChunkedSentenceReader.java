package jp.ac.jaist.nguyenlab.util;

import jp.ac.jaist.nguyenlab.extractor.mapper.BufferedReaderIterator;
import jp.ac.jaist.nguyenlab.extractor.mapper.ChunkedSentenceIterator;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by chientran on 2017/01/04.
 */
public class ChunkedSentenceReader {
    private Reader reader;
    private BufferedReaderIterator it;
    private ChunkedSentenceIterator sentenceIterator;
    public ChunkedSentenceReader(Reader in) throws IOException{
        this.reader = in;
        it = new BufferedReaderIterator(reader);
        sentenceIterator = new ChunkedSentenceIterator(it);
    }

    public Iterable<ChunkedSentence> getChunkedSentences() throws IOException{
        return new IterableAdapter<ChunkedSentence>(sentenceIterator);
    }

    public ChunkedSentenceIterator getChunkedSentenceIterator(){
        return this.sentenceIterator;
    }
}
