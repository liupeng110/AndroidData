package csh.tiro.cc;

/**
 * Created by 64217 on 2017/7/12.
 */

public class Libmad {
    static {
        System.loadLibrary("mad");
    }
    public static native boolean decodeFile(String mp3File,String pcmFile);
}
