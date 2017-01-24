package jp.ac.jaist.nguyenlab.sequence;

import com.google.common.base.Joiner;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chientran on 2017/01/04.
 */
public class RegexPattern {
    private String[] patternSymbols;
    private String[] patternTokens;
    private String[] patternLayerNames;
    private int patternLength;

    private String patternString;

    private PatternTokenizer tokenizer;

    private HashMap<String, Character> mappingTable;
    private Set<String> alphabets;

    private List<String> layerNames;
    private Map<String, Set<String>> layerAlphabets;

    private Encoder encoder;
    private String encodedPatternString;
    private Pattern encodedPattern;

    public RegexPattern(String patternString){
        this.patternString = patternString;
        tokenizePattern();
        buildMappingTable();
        encodePattern();
    }

    public void tokenizePattern(){
        tokenizer = new PatternTokenizer();
        patternTokens = tokenizer.tokenize(patternString);
        patternLength = patternTokens.length;

        patternSymbols = new String[patternLength];
        patternLayerNames = new String[patternLength];

        for (int i=0; i<patternLength; i++){
            String token = patternTokens[i];
            if (tokenizer.isSymbolLayerName(token)){
                String[] splitToken = token.split("_");
                patternSymbols[i] = splitToken[0];
                patternLayerNames[i] = splitToken[1];
            }else{
                patternSymbols[i] = null;
                patternLayerNames[i] = null;
            }
        }
    }

    public void buildMappingTable(){
        mappingTable = new HashMap<String, Character>();
        layerNames = new ArrayList<String>();
        layerAlphabets = new HashMap<String, Set<String>>();

        for (int i=0; i<patternLength; i++){
            String token = patternTokens[i];
            String symbol = patternSymbols[i];
            String layerName = patternLayerNames[i];

            if (layerName != null) {
                if (!layerAlphabets.containsKey(layerName)) {
                    layerNames.add(layerName);
                    layerAlphabets.put(layerName, new HashSet<String>());
                }
                if (symbol != null){
                    layerAlphabets.get(layerName).add(symbol);
                }
            }
        }

        List<Set<String>> symbols = new ArrayList<Set<String>>();

        for (String layerName: layerNames){
            symbols.add(layerAlphabets.get(layerName));
        }
        encoder = new Encoder(symbols);
    }

    /*
    Our expression is much simpler than ReVerb, so we do not use the complicated Encoder like ReVerb
    Accepted char: [ ] + ? *
     */
    public void encodePattern() throws SequenceException{
        String[] encodedTokens = new String[patternLength];

        for (int i=0; i<patternLength; i++){
            String symbol = patternSymbols[i];
            String layerName = patternLayerNames[i];

            if (symbol == null){
                encodedTokens[i] = patternTokens[i];
            }else{
                int layerIndex = layerNames.indexOf(layerName);
                String encodedString = new String(encoder.encodeLayer(layerIndex, symbol));

                encodedTokens[i] = "[" + Pattern.quote(encodedString) + "]";
            }
        }

        encodedPatternString = Joiner.on("").join(encodedTokens);
        encodedPattern = Pattern.compile(encodedPatternString);
    }

    public Matcher matcher(LayeredSequence sequence){
        String encodedString = encodeSequence(sequence);
        Matcher m = encodedPattern.matcher(encodedString);
        return m;
    }

    public String encodeSequence(LayeredSequence sequence){
        int sequenceLength = sequence.getLength();
        char[] encodedTokens = new char[sequenceLength];

        for (int i=0; i<sequenceLength; i++){
            String[] arr = new String[layerNames.size()];
            for (int j=0; j<layerNames.size(); j++){
                String layerName = layerNames.get(j);
                arr[j] = sequence.getLayerValue(layerName, i);
            }

            encodedTokens[i] = encoder.encodeTuple(arr);
        }

        return new String(encodedTokens);
    }

    public Pattern getEncodedPattern(){
        return encodedPattern;
    }

    public static void main(String[] args) throws Exception {
        RegexPattern regexPattern = new RegexPattern("RB_pos? IN_pos? VBN_pos VP_chunk");
        System.out.println(regexPattern.encodedPatternString);
    }

}
