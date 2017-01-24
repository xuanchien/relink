package jp.ac.jaist.nguyenlab.sequence;

import com.google.common.base.Joiner;

import java.util.HashMap;

/**
 * Created by chientran on 2017/01/05.
 */
public class LayeredSequence {
    private HashMap<String, String[]> layerData;
    private int length;

    public LayeredSequence(int length){
        this.length = length;
        layerData = new HashMap<String, String[]>();
    }

    public void addLayer(String layerName, String[] values){
        assert values.length == length;

        if (!layerData.containsKey(layerName)){
            layerData.put(layerName, new String[length]);
        }

        String[] singleLayerData = layerData.get(layerName);

        for (int i=0; i<length; i++){
            singleLayerData[i] = values[i];
        }
    }

    public String getLayerValue(String layerName, int index){
        if (!layerData.containsKey(layerName)){
            throw new SequenceException(String.format("Layer %s does not exist", layerName));
        }else{
            return layerData.get(layerName)[index];
        }
    }

    public int getLength(){
        return length;
    }

    public String toString(String layerName, String delimiter){
        if (!layerData.containsKey(layerName)){
            throw new SequenceException(layerName + " does not exist");
        }

        String[] values = layerData.get(layerName);
        return Joiner.on(delimiter).join(values);
    }

    public String toString(String layerName){
        return toString(layerName, " ");
    }

    /*
    Merge all continuous nodes into a single node and
    use information from *dominantNode* as the informatin for the new node
     Ex: [I/NN] [want/VB] [to/TO] [go/VB]
     mergeTokens(1, 3, 1) => [I/NN] [want to / VB] [go/VB]
     */
    public void mergeTokens(String layerName, int start, int end){
        mergeTokens(layerName, start, end, null);
    }

    /*
    Merge nodes in a layer together and use different name (instead of joining old names)
     */
    public void mergeTokens(String layerName, int start, int end, String spanName){
        assert start >= 0;
        String[] oldValues = layerData.get(layerName);
        assert end <= oldValues.length;

        int spanLength = end - start;
        String[] selectedSpan = new String[spanLength];

        length = oldValues.length - spanLength + 1;
        String[] newValues = new String[length];


        for (int i=0; i<start; i++){
            newValues[i] = oldValues[i];
        }

        for (int i=0; i<spanLength; i++){
            selectedSpan[i] = oldValues[i+start];
        }
        if (spanName != null) {
            newValues[start] = spanName;
        }else{
            newValues[start] = Joiner.on(" ").join(selectedSpan);
        }

        for (int i=end; i<oldValues.length; i++){
            newValues[i-spanLength+1] = oldValues[i];
        }
        layerData.put(layerName, newValues);
    }
}
