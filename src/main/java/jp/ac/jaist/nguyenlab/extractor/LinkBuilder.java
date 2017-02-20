package jp.ac.jaist.nguyenlab.extractor;

import edu.washington.cs.knowitall.commonlib.Range;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chientran on 2017/01/05.
 */
public class LinkBuilder {
    private ChunkedSentence sentence;
    private ArrayList<LinkableNode> nodes;
    private int sequenceLength;
    private RelationExtractor extractor;

    public LinkBuilder(ChunkedSentence sentence){
        this.sentence = sentence;
        sequenceLength = sentence.getLength();
        extractor = new RelationExtractor(sentence);
    }

    public List<ChunkedBinaryExtraction> extractRelations(){
        buildLinkableNodes();
        addExtraLinks();
        identifyDisputeNounPhrases();
        buildLinks();

        return extractVerbsWithNounPhrases();
    }

    private void buildLinkableNodes(){
        nodes = new ArrayList<LinkableNode>(sequenceLength);

        for (int i=0; i<sequenceLength; i++){
            String token = sentence.getLayerValue(ChunkedSentence.TOKEN_LAYER, i);
            String pos = sentence.getLayerValue(ChunkedSentence.POS_LAYER, i);
            String groupName = sentence.getLayerValue(ChunkedSentence.GROUP_LAYER, i);
            Range range = sentence.getRangeAt(i);

            LinkableNode node = new LinkableNode(token, pos, groupName, range);
            nodes.add(node);
        }
    }

    private void addExtraLinks(){
        for (int i=0; i<sequenceLength; i++){
            LinkableNode node = nodes.get(i);
            if (node.isWhModifier() && i>0){
                node.disableAvailableLinks();
                LinkableNode prevNode = nodes.get(i-1);
                if ((prevNode.isComma() || prevNode.isNoneGroup()) && i > 1){
                    prevNode = nodes.get(i-2);
                }

                if (prevNode.isNP()){
                    node.disableAvailableLinks();
                    prevNode.increaseAvailableLinks();
                }
            }
        }
    }

    private void identifyDisputeNounPhrases(){
        for (int i=1; i<sequenceLength-1; i++){
            LinkableNode node = nodes.get(i);
            LinkableNode prevNode = nodes.get(i-1);
            if (i > 1 && prevNode.isQuote()){
                prevNode = nodes.get(i-2);
            }

            LinkableNode nextNode = nodes.get(i+1);
            if (i < sequenceLength-2 && nextNode.isQuote()){
                nextNode = nodes.get(i+2);
            }

            if (node.isNP() && (prevNode.isActiveVerb() || prevNode.isPP()) && nextNode.isActiveVerb()){
                node.markAsDispute();
            }
        }
    }

    private void makeConnection(LinkableNode node1, LinkableNode node2){
        node1.linkRight(node2);
        node2.linkLeft(node1);
    }

    private void buildLinks(){
        for (int i=0; i<sequenceLength; i++){
            LinkableNode node = nodes.get(i);
            LinkableNode prevNode;
            LinkableNode nextNode;

            if (node.isVerb()){
                if (i > 0){
                    prevNode = nodes.get(i-1);
                    if (prevNode.isQuote() && i > 1){
                        prevNode = nodes.get(i-2);
                    }

                    LinkableNode leftNp = null;
                    //handle "and" token
                    if (prevNode.hasToken("and")){
                        leftNp = findArg1OfVerbType(0, i-2, node.getGroupName());
                        if (leftNp != null){
                            leftNp.increaseAvailableLinks();
                            makeConnection(leftNp, node);
                        }
                    }else{
                        if (node.isActiveVerb()){
                            if (prevNode.isNP() && prevNode.isAvailable()){
                                makeConnection(prevNode, node);
                            }else{
                                leftNp = findAvailableNounPhrase(0, i-2);
                                if (leftNp != null){
                                    makeConnection(leftNp, node);
                                }else if (prevNode.isDispute()){
                                    prevNode.removeLeftLinks();
                                    makeConnection(prevNode, node);
                                }
                            }
                        }else{
                            if (node.isPPVerb() && prevNode.isComma() && i>=2){
                                prevNode = nodes.get(i-2);
                            }
                            if (prevNode.isNP()){
                                prevNode.increaseAvailableLinks();
                                makeConnection(prevNode, node);
                            }
                        }
                    }

                }
                if (i < sequenceLength - 1){
                    nextNode = nodes.get(i+1);

                    if (nextNode.isNP() || nextNode.isPP()){
                        node.linkRight(nextNode);
                        nextNode.linkLeft(node);
                    }
                }
            } else if (node.isPP()){
                if (i > 0){
                    prevNode = nodes.get(i-1);
                    if (prevNode.isNP()){
                        prevNode.increaseAvailableLinks();
                        makeConnection(prevNode, node);
                    }
                }
                if (i < sequenceLength - 1){
                    nextNode = nodes.get(i+1);
                    if (nextNode.isNP()){
                        makeConnection(node, nextNode);
                    }
                }
            }
        }
    }

    private LinkableNode findAvailableNounPhrase(int start, int end){
        boolean hasSeenVerb = false;
        for (int i = end; i>=start; i--){
            LinkableNode node = nodes.get(i);
            //it should have at least 1 connection already
            if (node.isNP() && node.isAvailable()){
//                return node;
                if (node.hasConnectionToVerb(LinkableNode.RIGHT_MODE)){
                    return node;
                }else{
                    if (!hasSeenVerb){
                        return node;
                    }
//                    LinkableNode rightNode = nodes.get(i+1);
//                    if (!node.hasLeftNode() && rightNode.isNP() && rightNode.hasRightNode()){
//                        return node;
//                    }else{
//                        break;
//                    }
                }
            }
            if (node.isActiveVerb()){
                hasSeenVerb = true;
            }
        }

        return null;
    }

    private LinkableNode findArg1(int start, int end){
        LinkableNode node;
        for (int i=end; i>=start; i--){
            node = nodes.get(i);
            if (node.isVerb() && node.hasLeftNode()){
                return node.getFirstLeftNode();
            }
        }

        return null;
    }

    private LinkableNode findArg1OfVerbType(int start, int end, String groupName){
        LinkableNode node;
        for (int i=end; i>=start; i--){
            node = nodes.get(i);
            if (node.isVerb() && node.getGroupName().equals(groupName) && node.hasLeftNode()){
                return node.getFirstLeftNode();
            }
        }

        return null;
    }

    private ArrayList<ChunkedBinaryExtraction> extractVerbsWithNounPhrases(){
        ArrayList<ChunkedBinaryExtraction> allRelations = new ArrayList<ChunkedBinaryExtraction>();
        for (LinkableNode node: nodes){
            ArrayList<ChunkedBinaryExtraction> nodeRelations = extractor.extractFromNode(node);
            allRelations.addAll(nodeRelations);
        }

        return allRelations;
    }
}
