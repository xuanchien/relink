package jp.ac.jaist.nguyenlab.extractor;

import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;

/**
 * Created by chientran on 2017/01/04.
 */
public class VerbIdentifier {
    private static final String ADVERB_PART ="[RP_pos RB_pos]*";
    private static final String CHUNK_PART = "[RP_pos RB_pos (B-VP_chunk? I-VP_chunk)]*";
    private static final String SUFFIX_ADVERB_PART = "[RP_pos RB_pos B-PRT_chunk B-ADJP_chunk]*";
    private static final String RELATED_VP = "[RP_pos RB_pos B-PRT_chunk B-ADJP_chunk (I-VP_chunk*)]*";

    public static final String ACTIVE_VERB =
                        // Optional adverb
                        ADVERB_PART + " " +
                        // Modal or other verbs
                        "[MD_pos VB_pos VBD_pos VBP_pos VBZ_pos] " +
                        RELATED_VP;

    public static final String GERUND_VERB =
            ADVERB_PART + " " +
                    "VBG_pos " +
                    RELATED_VP;

    public static final String PAST_PARTICIPLE_VERB =
            ADVERB_PART + " " +
                    "VBN_pos " +
                    RELATED_VP;
    public static final String TO_VERB =
            ADVERB_PART + " " +
                    "TO_pos " +
                    ADVERB_PART + " " +
                    "[VB_pos VBP_pos] " +
                    RELATED_VP;


    public static final String EXTENDED_ACTIVE_VERB =
            "VP-A_group [VP-TO_group VP-G_group VP-P_group]? (IN_pos VP-G_group)*";

    public static final String EXTENDED_GERUND_VERB =
            "VP-G_group [VP-TO_group VP-A_group VP-P_group]? (IN_pos VP-G_group)*";

    public static final String EXTENDED_PAST_PARTICIPLE_VERB =
            "VP-P_group [VP-TO_group VP-G_group VP-A_group]? (IN_pos VP-G_group)*";

    public static final String EXTENDED_TO_VERB =
            "VP-TO_group [VP-A_group VP-G_group VP-P_group]? (IN_pos VP-G_group)*";

    public static final String EXTENDED_ACTIVE_VERB_WITH_PP =
            "VP-A_group IN_pos+ (?=[IN_pos])";
    public static final String EXTENDED_GERUND_VERB_WITH_PP =
            "VP-G_group IN_pos+ (?=[IN_pos])";
    public static final String EXTENDED_TO_VERB_WITH_PP =
            "VP-TO_group IN_pos+ (?=[IN_pos])";
    public static final String EXTENDED_PAST_VERB_WITH_PP =
            "VP-P_group IN_pos+ (?=[IN_pos])";

    public void identifyVerb(ChunkedSentence sentence){
        TokenMerger.findAndMerge(sentence, TO_VERB, "VP-TO");
        TokenMerger.findAndMerge(sentence, ACTIVE_VERB, "VP-A");
        TokenMerger.findAndMerge(sentence, GERUND_VERB, "VP-G");
        TokenMerger.findAndMerge(sentence, PAST_PARTICIPLE_VERB, "VP-P");

        TokenMerger.findAndMerge(sentence, EXTENDED_TO_VERB, "VP-TO");
        TokenMerger.findAndMerge(sentence, EXTENDED_ACTIVE_VERB, "VP-A");
        TokenMerger.findAndMerge(sentence, EXTENDED_GERUND_VERB, "VP-G");
        TokenMerger.findAndMerge(sentence, EXTENDED_PAST_PARTICIPLE_VERB, "VP-P");

        TokenMerger.findAndMerge(sentence, EXTENDED_ACTIVE_VERB_WITH_PP, "VP-A");
        TokenMerger.findAndMerge(sentence, EXTENDED_GERUND_VERB_WITH_PP, "VP-G");
        TokenMerger.findAndMerge(sentence, EXTENDED_TO_VERB_WITH_PP, "VP-TO");
        TokenMerger.findAndMerge(sentence, EXTENDED_PAST_VERB_WITH_PP, "VP-P");
    }
}
