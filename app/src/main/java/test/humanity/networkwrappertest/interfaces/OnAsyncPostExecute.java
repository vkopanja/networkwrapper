package test.humanity.networkwrappertest.interfaces;

/**
 * Created by vkopanja on 19/08/2015.
 */
public interface OnAsyncPostExecute {

    /**
     * Used in the {@link android.app.Activity} so we can disable loaders etc.
     * @param result
     */
    void onAsyncResult(String result);

}
