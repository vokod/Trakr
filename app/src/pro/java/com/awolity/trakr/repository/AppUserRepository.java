package com.awolity.trakr.repository;

import android.support.annotation.NonNull;

import com.awolity.trakrutils.MyLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    public void deleteUser(){
        FirebaseUser user = getAppUser();
        for (AppUserStatusListener listener : appUserStatusListeners) {
            listener.onDeleteAccount();
        }
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MyLog.d(TAG, "User account deleted.");
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
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

        void onDeleteAccount();
    }
}
