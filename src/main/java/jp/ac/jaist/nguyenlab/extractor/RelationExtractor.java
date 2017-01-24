package jp.ac.jaist.nguyenlab.extractor;

import com.google.common.base.Joiner;
import edu.washington.cs.knowitall.commonlib.Range;
import jp.ac.jaist.nguyenlab.nlp.ChunkedSentence;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by chientran on 2017/01/06.
 */
public class RelationExtractor {
    private ChunkedSentence sentence;

    public RelationExtractor(ChunkedSentence sentence){
        this.sentence = sentence;
    }

    private Set<String> rightChainFilter = new HashSet<String>(
            Arrays.asList("NP", "PP", "VP-TO")
    );

    private int MAX_CHAIN_OF_NODE = 6;

    public ArrayList<ChunkedBinaryExtraction> extractFromNode(LinkableNode node){
        ArrayList<ChunkedBinaryExtraction> relationData =new ArrayList<ChunkedBinaryExtraction>();

        ChunkedArgumentExtraction arg1;

        if (node.isActiveVerb() || node.isGerundVerb() || node.isPPVerb()){
            if (node.hasLeftNode() && node.hasRightNode()){
                LinkableNode leftNode = node.getFirstLeftNode();
                LinkableNode rightNode = node.getFirstRightNode();

                arg1 = getExtendedNPToken(leftNode);

                extractRelations(relationData, arg1, node);
            }
        }
        return relationData;
    }

    /*
    We would want to extend First Argument in case it is linked with other Nouns
    Ex: [NP A vaccination] [PP for] [NP Zika] [VP is being developed] [PP by] [NP the US]
    Simple Arg1: [A vaccination]
    Extended Arg: [A vaccination for Zika]
     */
    private ChunkedArgumentExtraction getExtendedNPToken(LinkableNode arg1Node){
        String arg1Token = arg1Node.getToken();
        int startRange = arg1Node.getRange().getStart();
        int endRange = arg1Node.getRange().getEnd();

        for (LinkableNode node: arg1Node.getRightNodes()){
            if (node.isPP()){
                LinkableNode nodeAfterPP = node.getFirstRightNode();

                if (nodeAfterPP.isNP()){
                    arg1Token += " " + node.getToken() + " " + getExtendedNPToken(nodeAfterPP);
                    endRange = nodeAfterPP.getRange().getEnd();

                    break;
                }
            }
        }

        Range range = new Range(startRange, endRange - startRange);

        ChunkedArgumentExtraction argumentExtraction = new ChunkedArgumentExtraction(sentence, arg1Token, range);
        return argumentExtraction;
    }


    private void extractRelations(List<ChunkedBinaryExtraction> relationAndArg2Data,
                                  ChunkedArgumentExtraction arg1,
                                  LinkableNode relNode){

        String relName = relNode.getToken();
        String arg2Text = "";
        String newRelName = "";

        ChunkedExtraction rel;
        ChunkedArgumentExtraction arg2;

        LinkableNode node_0 = relNode.getFirstRightNode();

        if (node_0.isNP()){
            rel = new ChunkedExtraction(this.sentence, relName, relNode.getRange());
            arg2 = new ChunkedArgumentExtraction(this.sentence, node_0.getToken(), node_0.getRange());
            addRelationToList(relationAndArg2Data, arg1, rel, arg2);

            relName += " " + node_0.getToken();
            for (LinkableNode node_1: node_0.getRightNodes()){
                if (node_1.isPP() && node_1.hasRightNode()){
                    LinkableNode node_2 = node_1.getFirstRightNode();
                    if (node_2 != null && node_2.isNP()){
                        newRelName = relName + " " + node_1.getToken();

                        rel = new ChunkedExtraction(this.sentence, newRelName, getRangeFromNodes(relNode, node_1));
                        arg2 = new ChunkedArgumentExtraction(this.sentence, node_2.getToken(), node_2.getRange());
                        addRelationToList(relationAndArg2Data, arg1, rel, arg2);
                    }
                }else if (node_1.isToVerb() && node_1.hasRightNode()){
                    LinkableNode node_2 = node_1.getFirstRightNode();
                    if (node_2.isPP() && node_2.hasRightNode()) {
                        LinkableNode node_3 = node_2.getFirstRightNode();
                        if (node_3.isNP()){
                            newRelName = relName + " " + node_1.getToken() + " " + node_2.getToken();
                            rel = new ChunkedExtraction(this.sentence, newRelName, getRangeFromNodes(relNode, node_2));
                            arg2 = new ChunkedArgumentExtraction(this.sentence, node_3.getToken(), node_3.getRange());
                            addRelationToList(relationAndArg2Data, arg1, rel, arg2);
                        }
                    }else if (node_2.isNP()){
                        newRelName = relName + " " + node_1.getToken();
                        rel = new ChunkedExtraction(this.sentence, newRelName, getRangeFromNodes(relNode, node_1));
                        arg2 = new ChunkedArgumentExtraction(this.sentence, node_2.getToken(), node_2.getRange());
                        addRelationToList(relationAndArg2Data, arg1, rel, arg2);
                    }
                }
            }
        }else if (node_0.isPP() && node_0.hasRightNode()){
            LinkableNode node_1 = node_0.getFirstRightNode();

            if (node_1.isNP()){
                relName += " " + node_0.getToken();

                rel = new ChunkedExtraction(this.sentence, relName, getRangeFromNodes(relNode, node_0));
                arg2 = new ChunkedArgumentExtraction(this.sentence, node_1.getToken(), node_1.getRange());
                addRelationToList(relationAndArg2Data, arg1, rel, arg2);

                relName += " " + node_1.getToken();
                for (LinkableNode node_2: node_1.getRightNodes()){
                    if (node_2.isPP() && node_2.hasRightNode()){
                        LinkableNode node_3 = node_2.getFirstRightNode();
                        if (node_3.isNP()){
                            newRelName = relName + " " + node_2.getToken();

                            rel = new ChunkedExtraction(this.sentence, newRelName, getRangeFromNodes(relNode, node_2));
                            arg2 = new ChunkedArgumentExtraction(this.sentence, node_3.getToken(), node_3.getRange());
                            addRelationToList(relationAndArg2Data, arg1, rel, arg2);
                        }
                    }else if (node_2.isToVerb() && node_2.hasRightNode()){
                        LinkableNode node_3 = node_2.getFirstRightNode();
                        if (node_3.isPP() && node_3.hasRightNode()) {
                            LinkableNode node_4 = node_3.getFirstRightNode();
                            if (node_4.isNP()){
                                newRelName = relName + " " + node_2.getToken() + " " + node_3.getToken();

                                rel = new ChunkedExtraction(this.sentence, newRelName, getRangeFromNodes(relNode, node_3));
                                arg2 = new ChunkedArgumentExtraction(this.sentence, node_4.getToken(), node_4.getRange());
                                addRelationToList(relationAndArg2Data, arg1, rel, arg2);
                            }
                        }else if (node_3.isNP()){
                            newRelName = relName + " " + node_2.getToken();
                            rel = new ChunkedExtraction(this.sentence, newRelName, getRangeFromNodes(relNode, node_2));
                            arg2 = new ChunkedArgumentExtraction(this.sentence, node_3.getToken(), node_3.getRange());
                            addRelationToList(relationAndArg2Data, arg1, rel, arg2);
                        }
                    }
                }
            }
        }

    }

    private Range getRangeFromNodes(LinkableNode node1, LinkableNode node2){
        int startRange = node1.getRange().getStart();
        int endRange = node2.getRange().getEnd();

        return new Range(startRange, endRange - startRange);
    }


//    private void extractRelAndArg2(List<ChunkedBinaryExtraction> relationAndArg2Data,
//                                   ChunkedArgumentExtraction arg1,
//                                   LinkableNode relNode){
//        String relName = relNode.getToken();
//        String arg2 = "";
//
//
//        List<LinkableNode> chainOfRightNodes = getChainOfNodes(relNode, MAX_CHAIN_OF_NODE);
//        int chainLength = chainOfRightNodes.size();
//
//        if (chainLength >= 1){
//            LinkableNode node_0 = chainOfRightNodes.get(0);
//            if (node_0.isNP()){
//                addRelationToList(relationAndArg2Data, arg1, relName, node_0.getToken());
//
//                if (chainLength >= 3){
//                    LinkableNode node_1 = chainOfRightNodes.get(1);
//                    LinkableNode node_2 = chainOfRightNodes.get(2);
//
//                    if (node_1.isPP() && node_2.isNP()){
//                        relName += " " + node_0.getToken() + " " + node_1.getToken();
//                        arg2 = node_2.getToken();
//
//                        addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//                    }else if (node_1.isToVerb() && node_2.isNP()){
//                        relName += " " + node_0.getToken() + " " + node_1.getToken();
//                        arg2 = node_2.getToken();
//
//                        addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//                    }else if (node_1.isToVerb() && node_2.isPP()){
//                        if (chainLength >= 4){
//                            LinkableNode node_3 = chainOfRightNodes.get(3);
//                            if (node_3.isNP()){
//                                relName += " " + node_0.getToken() + " " + node_1.getToken() + " " + node_2.getToken();
//                                arg2 = node_3.getToken();
//
//                                addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//                            }
//                        }
//
//                    }
//
//                }
//            }else if (node_0.isPP() && chainLength >= 2){
//                LinkableNode node_1 = chainOfRightNodes.get(1);
//                if (node_1.isNP()){
//                    relName += " " + node_0.getToken();
//                    arg2 = node_1.getToken();
//                    addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//
//                    if (chainLength >= 4){
//                        LinkableNode node_2 = chainOfRightNodes.get(2);
//                        LinkableNode node_3 = chainOfRightNodes.get(3);
//
//                        if (node_2.isPP() && node_3.isNP()){
//                            relName += " " +node_1.getToken() + " " + node_2.getToken();
//                            arg2 = node_3.getToken();
//                            addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//                        }else if(node_2.isToVerb()){
//                            relName += " " +node_1.getToken() + " " + node_2.getToken();
//                            arg2 = node_3.getToken();
//                            if (node_3.isNP()){
//                                addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//                            }else if (node_3.isPP() && chainLength >= 5){
//                                LinkableNode node_4 = chainOfRightNodes.get(4);
//                                if (node_4.isNP()){
//                                    relName += " " + node_3.getToken();
//                                    arg2 = node_4.getToken();
//                                    addRelationToList(relationAndArg2Data, arg1, relName, arg2);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    private String[] extractFromListByPattern(List<LinkableNode> nodes, String pattern){
        String[] patternTokens = pattern.split(" ");
        int length = patternTokens.length;

        String[] data = new String[length];

        for (int i=0; i<length; i++){
            LinkableNode node = nodes.get(i);
            if (node.getGroupName().equals(patternTokens[i])){
                data[i] = node.getToken();
            }else{
                return null;
            }
        }

        return data;
    }

    /*
    From the startNode, follow its links to extract many Nodes to the right of that nodes
     */
    private List<LinkableNode> getChainOfNodes(LinkableNode startNode, int maxChainLength){
        ArrayList<LinkableNode> chainOfNodes = new ArrayList<LinkableNode>();

        int i = 0;
        LinkableNode rightNode = startNode;

        while (i < maxChainLength){
            if (rightNode.hasRightNode()){
                rightNode = rightNode.getFirstRightNode(rightChainFilter);
                if (rightNode != null){
                    chainOfNodes.add(rightNode);
                    i++;
                }else{
                    break;
                }
            }else{
                break;
            }
        }

        return chainOfNodes;
    }

    private String[] extractArg2WithPP(LinkableNode arg2WithPP){
        String[] outcome = new String[]{"", ""};

        LinkableNode rightNode = arg2WithPP.getFirstRightNode();

        if (arg2WithPP.isPP() && rightNode != null && rightNode.isNP()){
            outcome[0] = arg2WithPP.getToken();
            outcome[1] = rightNode.getToken();
        }

        return outcome;
    }

    private String[] extractArg2WithToVerb(LinkableNode arg2WithToVerb){
        String[] outcome = new String[]{"", ""};
        LinkableNode rightNode = arg2WithToVerb.getFirstRightNode();

        if (arg2WithToVerb.isToVerb() && rightNode != null){
            if (rightNode.isNP()){
                outcome[0] = arg2WithToVerb.getToken();
                outcome[1] = rightNode.getToken();
            }else if (rightNode.isPP()){
                String[] data = extractArg2WithPP(rightNode);
                if (data[0].length() > 0){
                    outcome[0] = arg2WithToVerb.getToken() + " " + data[0];
                    outcome[1] = data[1];
                }
            }
        }

        return outcome;
    }

    private void addRelationToList(List<ChunkedBinaryExtraction> list,
                                   ChunkedArgumentExtraction arg1,
                                   ChunkedExtraction relName,
                                   ChunkedArgumentExtraction arg2){
        list.add(new ChunkedBinaryExtraction(arg1, relName, arg2));
    }
}
