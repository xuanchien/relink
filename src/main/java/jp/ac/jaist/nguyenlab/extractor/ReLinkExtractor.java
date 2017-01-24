package jp.ac.jaist.nguyenlab.extractor;

import com.google.common.base.Joiner;
import jp.ac.jaist.nguyenlab.extractor.mapper.ChunkedSentenceIterator;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;
import jp.ac.jaist.nguyenlab.nlp.OpenNlpSentenceChunker;
import jp.ac.jaist.nguyenlab.util.ChunkedSentenceReader;
import jp.ac.jaist.nguyenlab.util.Utils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;

public class ReLinkExtractor {
    static Logger logger = LogManager.getLogger("ReLinkExtractor");

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("t", "train", false,
                "Export data for training the conf function in ReVerb");

        CommandLineParser parser = new PosixParser();
        CommandLine params = parser.parse(options, args);

        BufferedReader reader;
        if (params.getArgs().length == 0) {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } else {
            reader = new BufferedReader(new FileReader(params.getArgs()[0]));
        }

        int sentenceCount = 0;
        int extractionCount = 0;

        System.err.print("Initializing extractor...");
        ReLinkExtractor extractor = new ReLinkExtractor();
        System.err.println("Done.");

        System.err.print("Initializing confidence function...");
        //ConfidenceFunction scoreFunc = new ReVerbOpenNlpConfFunction();
        System.err.println("Done.");

        System.err.print("Initializing NLP tools...");
        Utils.initializeNlpTools();
        System.err.println("Done.");

        VerbIdentifier verbIdentifier = new VerbIdentifier();
        NounIdentifier nounIdentifier = new NounIdentifier();
        PrepositionIdentifier ppIdentifier = new PrepositionIdentifier();

        ChunkedSentenceReader sentenceReader = new ChunkedSentenceReader(reader);

        long start = System.nanoTime();
        long chunkTime = 0;
        long extractTime = 0;

        ChunkedSentenceIterator sentIt = sentenceReader.getChunkedSentenceIterator();

        while (sentIt.hasNext()){
            ChunkedSentence sentence = sentIt.next();
            chunkTime += sentIt.getLastComputeTime();

            start = System.nanoTime();

//            if (!params.hasOption("t")){
//                System.out.println(sentence.toString(ChunkedSentence.TOKEN_LAYER));
//                logger.debug(sentence.toString(ChunkedSentence.POS_LAYER));
//                logger.debug(sentence.toString(ChunkedSentence.CHUNK_LAYER));
//            }

            sentenceCount++;
            nounIdentifier.identifyNoun(sentence);
            verbIdentifier.identifyVerb(sentence);
            ppIdentifier.identifyPreposition(sentence);
            nounIdentifier.extendNoun(sentence);

            //logger.debug("After merging");
            //logger.debug(sentence.toString(ChunkedSentence.TOKEN_LAYER));
            //logger.debug(sentence.toString(ChunkedSentence.GROUP_LAYER));

            LinkBuilder builder = new LinkBuilder(sentence);

            List<ChunkedBinaryExtraction> relations = builder.extractRelations();

            extractTime += System.nanoTime() - start;

            extractionCount += relations.size();

            for (int i=0; i<relations.size(); i++){
                if (params.hasOption("t")){
                    printDetailsExtraction(relations.get(i));
                }else{
                    System.out.println(
                            String.format("%f: %s", 1.0, relations.get(i).toString())
                    );
                }
            }

            logger.debug("========");
            System.out.println("");
        }

        System.err.println(
                String.format("%d sentences processed, %d relation extracted",
                        sentenceCount, extractionCount)
        );

        DecimalFormat fmt = new DecimalFormat("#.##");
        System.err.println("chunking: "
                + fmt.format(chunkTime / 1000.0 / 1000.0 / 1000.0) + " s, ");
        System.err.println("extraction: "
                + fmt.format(extractTime / 1000.0 / 1000.0 / 1000.0)
                + " s, ");
    }

    public static void printDetailsExtraction(ChunkedBinaryExtraction extraction){
        ChunkedExtraction rel = extraction.getRelation();
        ChunkedArgumentExtraction arg1 = extraction.getArg1();
        ChunkedArgumentExtraction arg2 = extraction.getArg2();

        ChunkedSentence sent = rel.getSentence();
        System.out.println(sent.toString(ChunkedSentence.TOKEN_LAYER));
        System.out.println(sent.toString(ChunkedSentence.POS_LAYER));
        System.out.println(sent.toString(ChunkedSentence.CHUNK_LAYER));
        System.out.println(arg1.toString());
        System.out.println(String.format("%d %d", arg1.getRange().getStart(), arg1.getRange().getLength()));
        System.out.println(rel.toString());
        System.out.println(String.format("%d %d", rel.getRange().getStart(), rel.getRange().getLength()));
        System.out.println(arg2.toString());
        System.out.println(String.format("%d %d", arg2.getRange().getStart(), arg2.getRange().getLength()));
    }
}