package jp.ac.jaist.nguyenlab.util;

import jp.ac.jaist.nguyenlab.extractor.mapper.BufferedReaderIterator;
import jp.ac.jaist.nguyenlab.extractor.mapper.ChunkedSentenceIterator;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;
import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chientran on 2017/01/04.
 */
public class Utils {
    public static final String tokenizerModelFile = "en-token.bin";
    public static final String taggerModelFile = "en-pos-maxent.bin";
    public static final String chunkerModelFile = "en-chunker.bin";
    public static final String sentDetectorModelFile = "en-sent.bin";

    public static InputStream getResourceAsStream(String resource) throws IOException{
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(resource);

        if (in == null){
            throw new IOException("Couldn't load resource: " + resource);
        }else{
            return in;
        }
    }

    public static void initializeNlpTools() throws IOException{
        getDefaultSentenceDetector();
        getDefaultTokenizer();
        getDefaultPosTagger();
        getDefaultChunker();
    }

    public static Tokenizer getDefaultTokenizer() throws IOException {
        return new TokenizerME(new TokenizerModel(
                getResourceAsStream(tokenizerModelFile)));
    }

    public static POSTagger getDefaultPosTagger() throws IOException {
        return new POSTaggerME(new POSModel(
                getResourceAsStream(taggerModelFile)));
    }

    public static Chunker getDefaultChunker() throws IOException {
        return new ChunkerME(new ChunkerModel(
                getResourceAsStream(chunkerModelFile)));
    }

    public static SentenceDetector getDefaultSentenceDetector()
            throws IOException {
        return new SentenceDetectorME(new SentenceModel(
                getResourceAsStream(sentDetectorModelFile)));
    }

}
