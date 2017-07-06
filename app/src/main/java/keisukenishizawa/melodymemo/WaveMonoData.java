package keisukenishizawa.melodymemo;

/**
 * Created by KeisukeNishizawa on 2017/03/10.
 */

public class WaveMonoData {
    public int fs; /* 標本化周波数 */
    public int bits; /* 量子化精度 */
    public int length; /* 音データの長さ */
    public double[] s; /* 音データ */


    WaveMonoData(int fs,int bits,int length,double[] s){
        this.fs = fs;
        this.bits = bits;
        this.length = length;
        this.s = s;
    }
}
