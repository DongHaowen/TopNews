package com.example.topnews.data;

import android.util.Log;

import com.example.topnews.MainActivity;
import com.example.topnews.bean.WebPackage;
import com.example.topnews.data.model.LoggedInUser;
import com.example.topnews.utils.RecordHandler;
import com.example.topnews.utils.UserClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    boolean lost = false;

    final static long waitTime = 3000;
    final static String TAG = "LoginData";

    private void reset(){
        lost = false;
    }

    public Result<LoggedInUser> signup(String username, String password) {
        reset();
        try {
            UserClient userClient = new UserClient();
            WebPackage bundle = new WebPackage();
            bundle.setItem(WebPackage.DATA_OP,WebPackage.OP_SIGNUP);
            bundle.setItem(WebPackage.DATA_ID,username);
            bundle.setItem(WebPackage.DATA_PASS,password);
            userClient.setWebPackage(bundle);
            userClient.run();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lost = true;
                }
            }, 3000);

            while (!lost && userClient.getResponse() == null){
                wait();
            }

            WebPackage response = userClient.getResponse();
            boolean signed = Boolean.valueOf(response.getString(WebPackage.OP_SIGNUP));
            if(signed){
                updateUser(username);
            }else{
                throw new Exception();
            }

            LoggedInUser user = new LoggedInUser(username);
            return new Result.Success<>(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error Sign up", e));
        }
    }

    public Result<LoggedInUser> login(String username, String password) {
        reset();
        try {
            // TODO: handle loggedInUser authentication
            UserClient userClient = new UserClient();
            WebPackage bundle = new WebPackage();
            bundle.setItem(WebPackage.DATA_OP,WebPackage.OP_LOGIN);
            bundle.setItem(WebPackage.DATA_ID,username);
            bundle.setItem(WebPackage.DATA_PASS,password);
            userClient.setWebPackage(bundle);
            userClient.run();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lost = true;
                }
            }, 3000);

            while (!lost && userClient.getResponse() == null){
                wait();
            }

            WebPackage response = userClient.getResponse();
            boolean logged = Boolean.valueOf(response.getString(WebPackage.OP_LOGIN));
            if(logged){
                updateUser(username);
            }else{
                throw new Exception();
            }

            LoggedInUser user = new LoggedInUser(username);
            return new Result.Success<>(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    private long getModify(){
        reset();
        UserClient userClient = new UserClient();
        WebPackage bundle = new WebPackage();
        bundle.setItem(WebPackage.DATA_OP,WebPackage.OP_GET_MODIFY);
        bundle.setItem(WebPackage.DATA_ID,MainActivity.user.getUserId());
        userClient.setWebPackage(bundle);
        userClient.run();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lost = true;
            }
        }, 3000);
        try {
            while (!lost && userClient.getResponse() == null) {
                wait();
            }
            return Long.valueOf(userClient.getResponse().getString(WebPackage.OP_GET_MODIFY));
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private void localToRemote(){
        Log.d(TAG,"Local to Remote");
        reset();
        UserClient userClient;
        WebPackage bundle;

        userClient = new UserClient();
        bundle = new WebPackage();
        bundle.setItem(WebPackage.DATA_OP,WebPackage.OP_UPDATE_MODIFY);
        bundle.setItem(WebPackage.DATA_ID,MainActivity.user.getUserId());
        bundle.setItem(WebPackage.DATA_EXTRA,String.valueOf(MainActivity.history.getModifyTime()));
        userClient.setWebPackage(bundle);
        userClient.run();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        userClient = new UserClient();
        bundle = new WebPackage();
        bundle.setItem(WebPackage.DATA_OP,WebPackage.OP_UPDATE_HISTORY);
        bundle.setItem(WebPackage.DATA_ID,MainActivity.user.getUserId());
        bundle.setItem(WebPackage.DATA_EXTRA,String.valueOf(gson.toJson(MainActivity.history,RecordHandler.class)));
        userClient.setWebPackage(bundle);
        userClient.run();

        userClient = new UserClient();
        bundle = new WebPackage();
        bundle.setItem(WebPackage.DATA_OP,WebPackage.OP_UPDATE_FAVORITE);
        bundle.setItem(WebPackage.DATA_ID,MainActivity.user.getUserId());
        bundle.setItem(WebPackage.DATA_EXTRA,String.valueOf(gson.toJson(MainActivity.favorite,RecordHandler.class)));
        userClient.setWebPackage(bundle);
        userClient.run();
    }

    private void remoteToLocal(){
        Log.d(TAG,"Remote to Local");
        reset();
        Gson gson = new GsonBuilder().create();
        UserClient hisClient = new UserClient();
        WebPackage hisBundle = new WebPackage();
        hisBundle.setItem(WebPackage.DATA_OP,WebPackage.OP_GET_HISTORY);
        hisBundle.setItem(WebPackage.DATA_ID,MainActivity.user.getUserId());
        hisClient.setWebPackage(hisBundle);
        hisClient.run();


        UserClient favClient = new UserClient();
        WebPackage favBundle = new WebPackage();
        favBundle.setItem(WebPackage.DATA_OP,WebPackage.OP_GET_FAVORITE);
        favBundle.setItem(WebPackage.DATA_ID,MainActivity.user.getUserId());
        favClient.setWebPackage(favBundle);
        favClient.run();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lost = true;
            }
        }, 3000);
        try {
            while (!lost && (hisClient.getResponse() == null || favClient.getResponse() == null) ) {
                wait();
            }
            if(lost) throw new Exception();
            String hisValue = hisClient.getResponse().getString(WebPackage.OP_GET_HISTORY);
            String favValue = hisClient.getResponse().getString(WebPackage.OP_GET_FAVORITE);
            if(hisValue != null) {
                MainActivity.history = gson.fromJson(hisValue, RecordHandler.class);
                MainActivity.history.save();
            }else {
                MainActivity.history = new RecordHandler("history");
                MainActivity.history.save();
            }
            if(favValue != null) {
                MainActivity.favorite = gson.fromJson(favValue, RecordHandler.class);
                MainActivity.favorite.save();
            } else {
                MainActivity.history = new RecordHandler("history");
                MainActivity.history.save();
            }
            return ;
        } catch (Exception e){
            e.printStackTrace();
            return ;
        }
    }

    public void remoteUpdate(LoggedInUser user){
        if (user.getUserId().equals("default"))
            return;
        // 时间戳判断，决定本地与远程的同步关系
        long remote = getModify();
        long local = MainActivity.history.getModifyTime();
        Log.d("RemoteTime:",String.valueOf(remote));
        Log.d("LocalTime",String.valueOf(local));
        if(local < remote){
            remoteToLocal();
        }else if(local > remote){
            localToRemote();
        }else{
            return;
        }
    }

    public void updateUser(final String username){
        Log.d(TAG,"Update User"+username);
        MainActivity.history.save();
        MainActivity.favorite.save();
        MainActivity.user = new LoggedInUser(username);
        MainActivity.base.setUser();
        MainActivity.history.updateUser();
        MainActivity.favorite.updateUser();
        MainActivity.history.load();
        MainActivity.favorite.load();
        MainActivity.base.columnChange();
        remoteUpdate(MainActivity.user);
    }

    public void logout() {
        // TODO: revoke authentication
        if(MainActivity.user == LoggedInUser.defaultUser) return;
        Log.d(TAG,"Log Out");
        // Change user to default user
        MainActivity.history.save();
        MainActivity.favorite.save();
        remoteUpdate(MainActivity.user);
        MainActivity.user = LoggedInUser.defaultUser;
        MainActivity.base.setUser();
        MainActivity.history.updateUser();
        MainActivity.favorite.updateUser();
        MainActivity.history.load();
        MainActivity.favorite.load();
        MainActivity.base.columnChange();
    }
}
