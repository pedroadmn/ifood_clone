package helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static helpers.FirebaseConfig.getAuthFirebase;

public class FirebaseUserHelper {

    public static String getUserId() {
        FirebaseAuth auth = getAuthFirebase();
        return auth.getCurrentUser().getUid();
    }

    public static FirebaseUser getCurrentUser() {
        FirebaseAuth auth = getAuthFirebase();
        return auth.getCurrentUser();
    }

    public static boolean updateUserType(String type) {
        try {
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(type)
                    .build();
            user.updateProfile(profile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
