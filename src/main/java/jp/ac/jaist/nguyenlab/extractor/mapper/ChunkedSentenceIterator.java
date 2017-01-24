package jp.ac.jaist.nguyenlab.extractor.mapper;

import com.google.common.collect.AbstractIterator;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;
import jp.ac.jaist.nguyenlab.nlp.OpenNlpSentenceChunker;
import jp.ac.jaist.nguyenlab.nlp.SentenceChunker;

import java.io.IOException;

/**
 * Created by chientran on 2017/01/04.
 */
public class ChunkedSentenceIterator extends AbstractIterator<ChunkedSentence> {
    private BufferedReaderIterator bufferedReaderIterator;
    private SentenceChunker chunker;
    private long lastComputeTime;

    public ChunkedSentenceIterator(BufferedReaderIterator bufferedIter) throws IOException{
        this.bufferedReaderIterator = bufferedIter;
        chunker = new OpenNlpSentenceChunker();
    }
    @Override
    protected ChunkedSentence computeNext() {
        while (this.bufferedReaderIterator.hasNext()){
            long start = System.nanoTime();
            String line = this.bufferedReaderIterator.next();
            ChunkedSentence chunkedSentence = chunker.chunkSentence(line);

            lastComputeTime = System.nanoTime() - start;

            return chunkedSentence;
        }
        return endOfData();
    }

    public long getLastComputeTime(){
        return lastComputeTime;
    }
}
