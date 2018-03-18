package GameGrabber;

import java.util.HashMap;

/**
 * Created by Dounx on 2018/3/18.
 */

public interface ResponseListener {
    void onDataReceivedSuccess(HashMap hashMap);
    void onDataReceivedFailed();
}
