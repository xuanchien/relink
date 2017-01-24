package jp.ac.jaist.nguyenlab.sequence;

/*
Code inherited from: https://github.com/knowitall/reverb/blob/master/core/src/main/java/edu/washington/cs/knowitall/sequence/LayeredPatternTokenizer.java
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chientran on 2017/01/04.
 */
public class PatternTokenizer {

    private final String tokenPatternStr = "([a-zA-Z0-9\\-.,:;?!\"'`$]+)_([a-zA-Z0-9\\-]+)";
    private final Pattern tokenPattern = Pattern.compile(tokenPatternStr);

    private final String[] metaChars = { "(", "[", "{", "\\", "^", "$", "|",
            "]", "}", ")", "?", "*", "+", ".", ":", "=", "!", "<", ">"};

    private Set<String> metaCharSet;

    public PatternTokenizer(){
        metaCharSet = new HashSet<String>();
        for (String metaChar : metaChars)
            metaCharSet.add(metaChar);
    }

    public String[] tokenize(String text){
        int lastMatch = 0;
        LinkedList<String> splitted = new LinkedList<String>();
        Matcher m = tokenPattern.matcher(text);

        while (m.find()){
            String candidate = text.substring(lastMatch, m.start());
            if (candidate.trim().length() > 0){
                addCharsOfString(text, candidate, splitted);
            }
            splitted.add(m.group());
            lastMatch = m.end();
        }

        String candidate = text.substring(lastMatch);
        if (candidate.trim().length() > 0){
            addCharsOfString(text, candidate, splitted);
        }

        return splitted.toArray(new String[splitted.size()]);
    }

    private void addCharsOfString(String text, String candidate, LinkedList<String> list){
        for (int i=0; i<candidate.length(); i++){
            String token = candidate.substring(i, i+1);
            if (isMetaChar(token)){
                list.add(token);
            }else if (token.trim().length() > 0){
                throw new SequenceException(String.format("Cannot tokenize pattern %s", text));
            }
        }
    }

    private boolean isMetaChar(String c){
        return metaCharSet.contains(c);
    }

    public boolean isSymbolLayerName(String text) {
        return tokenPattern.matcher(text).matches();
    }
}
