package com.awolity.trakr.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AppUserRepository {

    private static final String TAG = AppUserRepository.class.getSimpleName();
    private List<AppUserStatusListener> appUserStatusListeners;

    public AppUserRepository() {
        appUserStatusListeners = new ArrayList<>();
    }

    public FirebaseUser getAppUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean IsAppUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        for (AppUserStatusListener listener : appUserStatusListeners) {
            listener.onSignOut();
        }
    }

    public void signIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            for (AppUserStatusListener listener : appUserStatusListeners) {
                listener.onSignIn();
            }
        }
    }

    public String getAppUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public void setAppUserStatusListener(AppUserStatusListener listener) {
        appUserStatusListeners.add(listener);
    }

    public interface AppUserStatusListener {
        void onSignOut();

        void onSignIn();
    }
}
