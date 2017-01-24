package jp.ac.jaist.nguyenlab.extractor;

/**
 * Created by chientran on 2017/01/16.
 */
public class ChunkedBinaryExtraction {
    private ChunkedArgumentExtraction arg1;
    private ChunkedArgumentExtraction arg2;
    private ChunkedExtraction rel;

    public ChunkedBinaryExtraction(ChunkedArgumentExtraction arg1, ChunkedExtraction rel, ChunkedArgumentExtraction arg2){
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.rel = rel;
    }

    public String toString(){
        return String.format("(%s; %s; %s)", arg1.toString(), rel.toString(), arg2.toString());
    }

    public ChunkedExtraction getRelation(){
        return rel;
    }

    public ChunkedArgumentExtraction getArg1(){
        return arg1;
    }

    public ChunkedArgumentExtraction getArg2(){
        return arg2;
    }

}
