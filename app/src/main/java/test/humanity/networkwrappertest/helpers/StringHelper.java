package test.humanity.networkwrappertest.helpers;

/**
 * Created by vkopanja on 18/08/2015.
 */
public class StringHelper {

    /**
     * Returns if the string is either null or empty
     *
     * @param s
     * @return
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }
}