package jp.ac.jaist.nguyenlab.sequence;

import java.util.Arrays;

/**
 * Created by chientran on 2017/01/05.
 */
public class TokenTuple {
    private String[] data;
    public TokenTuple(String[] data){
        this.data = data;
    }

    public String[] getData(){
        return this.data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TokenTuple))
            return false;
        TokenTuple other = (TokenTuple) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }
}
