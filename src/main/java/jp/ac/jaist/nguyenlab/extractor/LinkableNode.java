package jp.ac.jaist.nguyenlab.extractor;

import edu.washington.cs.knowitall.commonlib.Range;

import java.util.*;

/**
 * Created by chientran on 2017/01/05.
 */
public class LinkableNode {
    private String token;
    private String posTag;
    private String groupName;

    public static final String ACTIVE_VERB = "VP-A";
    public static final String GERUND_VERB = "VP-G";
    public static final String PP_VERB = "VP-P";
    public static final String TO_VERB = "VP-TO";
    public static final String NOUN_PHRASE = "NP";
    public static final String PP = "PP";
    public static final String O_CHUNK = "O";

    public static final int LEFT_MODE = 0;
    public static final int RIGHT_MODE = 1;

    private String[] quoteCharacters = new String[]{"\"", "'", "''"};
    private static final Set<String> PP_EXCLUSION_LIST = new HashSet<String>(Arrays.asList("that"));

    private HashSet<String> quoteCharacterSet;

    private boolean dispute;
    private int availableLinks;

    private List<LinkableNode> leftNodes;
    private List<LinkableNode> rightNodes;
    private Range range;

    public LinkableNode(String token, String posTag, String groupName){
        this(token, posTag, groupName, new Range(0, 1));
    }

    public LinkableNode(String token, String posTag, String groupName, Range range){
        this.token = token;
        this.posTag = posTag;
        this.groupName = groupName;
        this.dispute = false;
        initializeAvailableLinks();
        leftNodes = new ArrayList<LinkableNode>();
        rightNodes = new ArrayList<LinkableNode>();

        quoteCharacterSet = new HashSet<String>();
        for (String s: quoteCharacters){
            quoteCharacterSet.add(s);
        }
        this.range = range;
    }


    public void initializeAvailableLinks(){
        if (isNP()){
            availableLinks = 1;
        }else if (!isNoneGroup()){
            availableLinks = 2;
        }else{
            availableLinks = 0;
        }
    }

    public void linkRight(LinkableNode otherNode){
        assert availableLinks > 0;
        decreaseAvailableLinks();
        rightNodes.add(otherNode);
    }

    public void linkLeft(LinkableNode otherNode){
        assert availableLinks > 0;
        decreaseAvailableLinks();
        leftNodes.add(otherNode);
    }

    public void removeLeftLinks(){
        availableLinks += leftNodes.size();
        for (LinkableNode n: leftNodes){
            n.breakConnectionWith(this, RIGHT_MODE);
        }
        leftNodes = new ArrayList<LinkableNode>();
    }

    private void breakConnectionWith(LinkableNode otherNode, int side){
        List<LinkableNode> checkingNodes;
        if (side == LEFT_MODE){
            checkingNodes = leftNodes;
        }else{
            checkingNodes = rightNodes;
        }

        int beforeRemovingCount = checkingNodes.size();
        checkingNodes.remove(otherNode);
        availableLinks += (beforeRemovingCount - checkingNodes.size());
    }

    public void increaseAvailableLinks(){
        availableLinks += 1;
    }

    private void decreaseAvailableLinks(){
        availableLinks -= 1;
    }

    public void disableAvailableLinks(){
        availableLinks = 0;
    }
    public void markAsDispute(){
        dispute = true;
    }

    public boolean isActiveVerb(){
        return this.groupName.equals(ACTIVE_VERB);
    }
    public boolean isToVerb(){
        return this.groupName.equals(TO_VERB);
    }

    public boolean isVerb(){
        return this.groupName.startsWith("VP");
    }
    public boolean isGerundVerb() { return this.groupName.equals(GERUND_VERB); }
    public boolean isPPVerb(){ return this.groupName.equals(PP_VERB); }

    public boolean isDispute(){
        return dispute;
    }

    public boolean isNP(){
        return this.groupName.equals(NOUN_PHRASE) && !isWhModifier();
    }

    public boolean isPP(){
        return this.groupName.equals(PP) && !PP_EXCLUSION_LIST.contains(token);
    }

    public boolean isNoneGroup(){
        return this.groupName.equals(O_CHUNK);
    }

    public boolean isWhModifier(){
        return this.posTag.equals("WP") || this.posTag.equals("WDT");
    }

    public boolean isComma(){
        return this.token.equals(",");
    }

    public boolean isQuote(){
        return quoteCharacterSet.contains(this.token);
    }

    public boolean isAvailable(){
        return this.availableLinks > 0;
    }

    public boolean hasLeftNode(){
        return this.leftNodes.size() > 0;
    }

    public boolean hasRightNode(){
        return this.rightNodes.size() > 0;
    }

    public List<LinkableNode> getRightNodes(){
        return rightNodes;
    }

    public LinkableNode getFirstRightNode(Set<String> filters){
        List<LinkableNode> filteredNodes = new ArrayList<LinkableNode>();
        for (LinkableNode node: rightNodes){
            if (filters.contains(node.groupName)){
                filteredNodes.add(node);
            }
        }

        if (filteredNodes.size() > 0){
            return filteredNodes.get(0);
        }else{
            return null;
        }
    }

    public LinkableNode getFirstLeftNode(){
        return this.leftNodes.get(0);
    }

    public LinkableNode getFirstRightNode(){
        return this.rightNodes.get(0);
    }

    public String getToken(){
        return token;
    }

    public String getGroupName(){
        return groupName;
    }

    public String getPosTag(){
        return posTag;
    }

    public boolean hasToken(String checkingToken){
        return token.toLowerCase().equals(checkingToken);
    }

    public boolean hasConnectionToVerb(int mode){
        List<LinkableNode> nodes;
        if (mode == RIGHT_MODE){
            nodes = rightNodes;
        }else{
            nodes = leftNodes;
        }

        for (LinkableNode n: nodes){
            if (n.isVerb()){
                return true;
            }
        }

        return false;
    }

    public Range getRange(){
        return this.range;
    }
}
