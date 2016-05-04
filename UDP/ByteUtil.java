package edu.spbspu.dcn.netcourse.UDP;

/**
 * Created by masha on 05.12.15.
 */

public class ByteUtil {
    public static int fromByteArray(byte[] bytes) {
        return bytes[0]<< 24 | (bytes[1] & 0xFF)<< 16 | (bytes[2] & 0xFF)<< 8 | (bytes[3] & 0xFF);
    }

    public static byte[] fromInt(int number) {
        byte[] result = new byte[4];
        result[0] = (byte) (number>> 24);
        result[1] = (byte) (number>> 16);
        result[2] = (byte) (number >> 8);
        result[3] = (byte) (number /*» 0*/);
        return result;
    }

    public static void test() {
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            byte[] bytes = ByteUtil.fromInt(i);
            int result = ByteUtil.fromByteArray(bytes);
            if (result != i)
                System.out.println(i);
            System.out.println("done");
        }
    }

    public static void main(String[] args) {

        test();
       /* int ind= 1000;
        byte[] bytes = fromInt(ind);
        for(int i = 0; i<bytes.length; i++){
            System.out.println();
        }
    }
    */
    }
}
