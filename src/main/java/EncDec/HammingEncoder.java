package EncDec;

/**
 * Created with IntelliJ IDEA.
 * User: Joe90
 * Date: 30/11/14
 * Time: 21:05
 * To change this template use File | Settings | File Templates.
 */
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;

public class HammingEncoder {
//   {3,  5,  7,  9,  11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31},
//   {3,  6,  7,  10, 11, 14, 15, 18, 19, 22, 23, 26, 27, 30, 31},
//   {5,  6,  7,  12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31},
//   {9,  10, 11, 12, 13, 14, 15, 24, 25, 26, 27, 28, 29, 30, 31},
//   {17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31}};

    private int[] parity;
    private int syndrome = 0;
    private int powersOf;
    private StringBuilder sb;
    List<List<Integer>> pl;

    public HammingEncoder(){
        this.sb = new StringBuilder();
        this.pl = new ArrayList<List<Integer>>();
    }

    public String encodeWord(String word) {
        clearAndAppendWord(word, this.sb);
        insertRedundantBits(this.sb);
        StringBuilder encodedWord = encodeDecodeWord(this.sb, true);
        return encodedWord.toString();
    }

    public String decodeWord(String word) {
        clearAndAppendWord(word, this.sb);
        StringBuilder decodedWord = encodeDecodeWord(this.sb, false);
        return detectAndCorrectError(decodedWord).toString();
    }

    public int getSyndrome() {
        return this.syndrome;
    }

    private StringBuilder encodeDecodeWord(StringBuilder sb, boolean isEncode) {
        List<List<Integer>> pl = generateHammingCode(sb.toString(), this.pl);
        this.powersOf= (int) Math.pow(2, pl.size()) / 2;
        this.parity = new int[ pl.size() ];

        for(int i = 0, powersOfTwo = 1 ; i < pl.size(); i++, powersOfTwo *= 2) {
            int y = 0;

            for(int j = 0; j < pl.get(i).size(); j++ ) {
                int thisPl = pl.get(i).get(j);
                int pli = thisPl == 0 ? thisPl : thisPl - 1 ;
                int bit = Integer.parseInt( String.valueOf( sb.toString().charAt( pli ) ) );

                y += bit;
            }

            if(isEncode){
                sb.replace( powersOfTwo - 1, powersOfTwo, Integer.toString( y % 2 ) );
            } else {
                parity[i] = y % 2;
            }

        }
        return sb;
    }

    private List generateHammingCode( String rawWord, List<List<Integer>> pl ) {
        pl.clear();
        List values = getArrayListOfPowers(rawWord);

        int parityLength = values.size();

        for(int i = 0; i < parityLength; i++) {
            int x, add, skip;
            add = skip = x = (Integer)values.get(i);
            pl.add(new ArrayList<Integer>());

            for(int pos = x; pos < rawWord.length()+1; pos++) {
                if(add == 0 && skip == 0){
                    add = skip = x;
                }

                if(add > 0){
                    if(x < pos){ pl.get(pl.size()-1).add(pos); }
                    add--;
                } else {
                    if(skip > 0){ skip--; }
                }
            }
        }

        return pl;
    }

    private List getArrayListOfPowers(String rawWord) {
        List<Integer> powersOfTwo = new ArrayList<Integer>();
        int powersOf = 1;

        for(int i = 0; powersOf < rawWord.length(); i++, powersOf *= 2){
            powersOfTwo.add(powersOf);
        }

        return powersOfTwo;
    }

    private void clearAndAppendWord(String word, StringBuilder sb) {
        sb.delete(0, sb.length());
        sb.append(word);
    }

    private String detectAndCorrectError(StringBuilder encodedWord) {
        int syndrome = computeSyndrome(encodedWord);
        this.syndrome = syndrome;
        StringBuilder correctedWord = correctError(encodedWord, syndrome);
        return stripRedundantBits(correctedWord).toString();
    }

    private int computeSyndrome(StringBuilder encodedWord) {
        int syndrome = 0;
        for( int i = 0, powersOfTwo = 1; i < parity.length; i++, powersOfTwo *= 2 ) {
            syndrome += getBinaryDigit( encodedWord, powersOfTwo - 1 ) == parity[i] ? 0 : powersOfTwo;
        }

        return syndrome;
    }

    private StringBuilder correctError(StringBuilder encodedWord, int syndrome) {
        if(syndrome != 0) {
            return encodedWord.replace( syndrome - 1, syndrome, String.valueOf(encodedWord.toString().charAt(syndrome - 1) == '1' ? "0" : "1" ) );
        }

        return encodedWord;
    }

    private void insertRedundantBits(StringBuilder unEncodedWord) {
        for(int i = 1; i <= unEncodedWord.length(); i*=2){
            unEncodedWord.insert( i - 1, 0 );
        }
    }

    private StringBuilder stripRedundantBits(StringBuilder sb) {
        for(int i = this.powersOf; i > 0; i/=2){
            sb.deleteCharAt( i - 1 );
        }

        return sb;
    }

    private int getBinaryDigit( StringBuilder binStr, int index ) {
        return Integer.parseInt( String.valueOf( binStr.toString().charAt(index) ) );
    }

}
