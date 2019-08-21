package iit.cnr.it.mailcheck;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by giacomo on 05/12/18.
 */

public interface SpamListener {
    void SpamCheckCompleted(boolean status, String result, int spamIndex);


}
