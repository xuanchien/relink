package jp.ac.jaist.nguyenlab.sequence;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * Created by chientran on 2017/01/05.
 */
public class Encoder {
    private HashMap<TokenTuple, Character> encodingTable;
    private ArrayList<Set<String>> alphabets;

    private final String UNK = "UNK";

    public Encoder(List<Set<String>> symbols){
        alphabets = new ArrayList<Set<String>>(symbols.size());

        int encodingTableSize = 1;

        for (Set<String> symbolSet: symbols){
            Set<String> alphabet = new HashSet<String>(symbolSet.size() +1);
            alphabet.add(UNK);

            for (String symbol: symbolSet){
                alphabet.add(symbol);
            }
            alphabets.add(alphabet);
            encodingTableSize *= alphabet.size();
        }

        encodingTable = new HashMap<TokenTuple, Character>(encodingTableSize);

        int i=0;

        for (List<String> tupleAr: Sets.cartesianProduct(alphabets)){
            TokenTuple tuple = new TokenTuple(tupleAr.toArray(new String[0]));
            encodingTable.put(tuple, Character.toChars(i)[0]);
            i++;
        }
    }

    public char encodeTuple(String[] tuple){
        return encodeMapped(mapToUnknown(tuple));
    }

    public char encodeMapped(String[] tuple){
        TokenTuple tupleObject = new TokenTuple(tuple);

        return encodingTable.get(tupleObject);
    }

    public char[] encodeLayer(int layerIndex, String value){
        List<Character> result = new ArrayList<Character>();

        for (TokenTuple tuple: encodingTable.keySet()){
            String[] data = tuple.getData();
            if (data[layerIndex].equals(value)){
                result.add(encodeMapped(data));
            }
        }

        char[] chResult = new char[result.size()];
        for (int i=0; i<chResult.length; i++){
            chResult[i] = result.get(i);
        }

        return chResult;
    }

    public String[] mapToUnknown(String[] tuple){
        String[] result = new String[tuple.length];

        for (int i=0; i<result.length; i++){
            Set<String> knowns = alphabets.get(i);

            // The tuple is malformed if it contains UNK
            if (tuple[i].equals(UNK)) {
            } else if (knowns.contains(tuple[i])) {
                result[i] = tuple[i];
            } else {
                result[i] = UNK;
            }
        }

        return result;
    }
}
