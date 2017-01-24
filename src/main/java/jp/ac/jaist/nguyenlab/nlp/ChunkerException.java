package jp.ac.jaist.nguyenlab.nlp;

/**
 * Created by chientran on 2017/01/04.
 */
public class ChunkerException extends RuntimeException {
    public ChunkerException(String message, Exception cause){
        super(message, cause);
    }
}
