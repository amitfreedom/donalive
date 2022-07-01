package com.mobile.donalive.agora.openvcall.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.donalive.NoteApplication;
import com.mobile.donalive.R;
import com.mobile.donalive.agora.firebase.ChatMessageModel;
import com.mobile.donalive.agora.firebase.CommentAdapter;
import com.mobile.donalive.agora.firebase.GiftAdapter;
import com.mobile.donalive.agora.firebase.GiftModel;
import com.mobile.donalive.agora.firebase.GiftsListModel;
import com.mobile.donalive.agora.firebase.HeartModel;
import com.mobile.donalive.agora.firebase.PKLiveUserAdapter;
import com.mobile.donalive.agora.firebase.RequestMultiLiveAdapter;
import com.mobile.donalive.agora.firebase.SendPkGiftMode;
import com.mobile.donalive.agora.firebase.UserJoinedAdapter;
import com.mobile.donalive.agora.firebase.ViewerAdapter;
import com.mobile.donalive.agora.heartview.HeartDrawable;
import com.mobile.donalive.agora.openvcall.model.AGEventHandler;
import com.mobile.donalive.agora.openvcall.model.ConstantApp;
import com.mobile.donalive.agora.openvcall.model.DuringCallEventHandler;
import com.mobile.donalive.agora.openvcall.ui.layout.GridVideoViewContainer;
import com.mobile.donalive.agora.openvcall.ui.layout.SmallVideoViewAdapter;
import com.mobile.donalive.agora.openvcall.ui.layout.SmallVideoViewDecoration;
import com.mobile.donalive.agora.propeller.Constant;
import com.mobile.donalive.agora.propeller.UserStatusData;
import com.mobile.donalive.agora.propeller.VideoInfoData;
import com.mobile.donalive.agora.propeller.ui.RecyclerItemClickListener;
import com.mobile.donalive.agora.propeller.ui.RtlLinearLayoutManager;
import com.mobile.donalive.databinding.ActivityPkLiveBinding;
import com.mobile.donalive.databinding.DialogGiftBinding;
import com.mobile.donalive.databinding.DialogLiveUserJoinedBinding;
import com.mobile.donalive.databinding.DialogPkLiveUsersBinding;
import com.mobile.donalive.databinding.DialogReceivedGiftBinding;
import com.mobile.donalive.databinding.DialogRequestMultiLiveBinding;
import com.mobile.donalive.databinding.DialogRequestMultiliveListBinding;
import com.mobile.donalive.databinding.DialogShowGiftBinding;
import com.mobile.donalive.databinding.ProfileBottomSheetBinding;
import com.mobile.donalive.modelclass.CreateLiveHistoryModel;
import com.mobile.donalive.modelclass.Details;
import com.mobile.donalive.modelclass.Dimaond;
import com.mobile.donalive.modelclass.GoLiveModelClass;
import com.mobile.donalive.modelclass.PKLiveUserModel;
import com.mobile.donalive.utils.AppConstants;
import com.mobile.donalive.utils.BackgroundService;
import com.mobile.donalive.utils.CommonUtils;
import com.mobile.donalive.utils.Singleton;
import com.mobile.donalive.viewmodels.VM;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.ChannelMediaInfo;
import io.agora.rtc.video.ChannelMediaRelayConfiguration;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


public class PKLiveActivity extends BaseActivity implements DuringCallEventHandler {

    public static final int LAYOUT_TYPE_DEFAULT = 0;
    public static final int LAYOUT_TYPE_SMALL = 1;
    private final static Logger log = LoggerFactory.getLogger(PKLiveActivity.class);
    // should only be modified under UI thread
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid
    private final Handler mUIHandler = new Handler();
    public int mLayoutType = LAYOUT_TYPE_DEFAULT;
    List<String> list = new ArrayList<>();
    private GridVideoViewContainer mGridVideoViewContainer;
    private RelativeLayout mSmallVideoViewDock;
    private volatile boolean mVideoMuted = false;
    private volatile boolean mAudioMuted = false;
    private volatile boolean mMixingAudio = false;
    private volatile int mAudioRouting = Constants.AUDIO_ROUTE_DEFAULT;
    private volatile boolean mFullScreen = false;
    private boolean mIsLandscape = false;
    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = firebaseDatabase.getReference().child("userInfo");

    private String getChannelName = "";
    private String getAccessToken = "";
    private String liveStatus = "";
    public static String liveType = "";
    private String name = "", image = "", count = "";

    private ActivityPkLiveBinding binding;
    private String otherUserId = "";
    private final List<ChatMessageModel> chatMessages = new ArrayList<>();
    private final List<GoLiveModelClass> viewerList = new ArrayList<>();
    private final List<GoLiveModelClass> banList = new ArrayList<>();
    private List<GoLiveModelClass> requestMultiLiveList = new ArrayList<>();
    private List<String> muteUsers = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private ViewerAdapter viewerAdapter;

    int[] drawableIds = {R.drawable.ic_red_heart, R.drawable.ic_blue_heart, R.drawable.ic_peach_heart, R.drawable.ic_white_heart, R.drawable.ic_pink_heart, R.drawable.ic_green_heart, R.drawable.ic_yello_heart,};
    private List<Drawable> drawablesList = new ArrayList<>();
    private RequestMultiLiveAdapter requestMultiLiveAdapter;
    AlertDialog alertDialog;
    BottomSheetDialog requestListMultiBottomSheet;
    private boolean isLiveConnected = false;
    private CreateLiveHistoryModel createLiveHistory;

    private CountDownTimer pkCountTime;
    private String pkUserId = "";
    private Boolean isPkConnected = false;
    private String liveId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPkLiveBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        firebaseDatabase.goOnline();

        getChannelName = getIntent().getStringExtra("channelName");
        liveType = getIntent().getStringExtra("liveType");
        liveStatus = getIntent().getStringExtra("liveStatus");
        getAccessToken = getIntent().getStringExtra("token");
        otherUserId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");
        count = getIntent().getStringExtra("count");

        if (!liveStatus.equalsIgnoreCase("host")) {
            hitCreateLiveHistoryApi(CommonUtils.Companion.getUserId(), getCurrentTime(), "", liveType);
        } else {
            liveId = getIntent().getStringExtra("liveId");

        }

        getPKLiveRequest();

        getBanListFirebase();
        getMuteUsers();
        initHeartDrawables();
        getHostCoinFirebase();

        binding.txtUserName.setText(name);
        binding.txtFollowers.setText(count);
        Glide.with(this).load(image).placeholder(R.drawable.ic_baseline_account_circle_24).into(binding.imgProfile);

    }

    String getHostCoinInfo;

    private void getHostCoinFirebase() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("currentDiamond").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getHostCoinInfo = snapshot.getValue(String.class);
                    binding.txtTotalDiamond.setText(CommonUtils.Companion.prettyCount(Long.parseLong(getHostCoinInfo)));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getGift() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.exists()) {
                    GiftModel giftModel = snapshot.getValue(GiftModel.class);
                    setUpdateCoinFirebase(giftModel);
//                    showGift(giftModel);
                    showGiftDialog(giftModel);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void showGiftDialog(GiftModel giftModel) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (countDownTimer != null) countDownTimer.cancel();

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        DialogReceivedGiftBinding dialogReceivedGiftBinding = DialogReceivedGiftBinding.inflate(LayoutInflater.from(this));
        alert.setView(dialogReceivedGiftBinding.getRoot());
        alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            alertDialog.show();
            Glide.with(PKLiveActivity.this).load(giftModel.getGiftPath()).into(dialogReceivedGiftBinding.imgShowGif);
        } catch (Exception e) {
        }


        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                Log.i("onTick: ", (l / 1000) + "");
            }

            @Override
            public void onFinish() {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").removeValue();
            }
        };
        countDownTimer.start();

    }

    private void setUpdateCoinFirebase(GiftModel giftModel) {
        Long totalCoin = Long.valueOf(getHostCoinInfo) + Long.valueOf(giftModel.getGiftCoin());
        HashMap<String, Object> data = new HashMap<>();
        data.put("currentDiamond", String.valueOf(totalCoin));
        ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").updateChildren(data);
    }

    CountDownTimer countDownTimer;

    private void showGift(GiftModel giftModel) {
        binding.lottieGift.setVisibility(View.GONE);
        try {
            Glide.with(this).load(giftModel.getGiftPath()).into(binding.lottieGift);
        } catch (Exception e) {

        }
//        binding.lottieGift.setAnimationFromUrl(giftModel.getGiftPath());
        binding.lottieGift.setVisibility(View.VISIBLE);
//        binding.lottieGift.playAnimation();

    }


    private final List<GoLiveModelClass> liveJoinedUserList = new ArrayList<>();

    private void getPKLiveRequest() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("PKLiveRequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    requestMultiLiveList.clear();
                    liveJoinedUserList.clear();

                    GoLiveModelClass hostUserDetails = new GoLiveModelClass();
                    hostUserDetails.setUserID(otherUserId);
                    hostUserDetails.setUserName(getChannelName);
                    hostUserDetails.setImage(image);
                    hostUserDetails.setName(name);
                    liveJoinedUserList.add(0, hostUserDetails);
                    GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        try {
                            goLiveModelClass = snapshot1.getValue(GoLiveModelClass.class);

                        } catch (Exception e) {

                        }

                        if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("1")) {
                            liveJoinedUserList.add(goLiveModelClass);
                        } else if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("")) {
                            requestMultiLiveList.add(goLiveModelClass);
                            if (CommonUtils.Companion.getUserId().equalsIgnoreCase(otherUserId)) {
                                openRequestDialogForMultiLive(goLiveModelClass);
                            }
                        }


                    }

                    if (userJoinedAdapter != null) {
                        userJoinedAdapter.notifyDataSetChanged();
                    }

                    setRequestListAdapter(requestMultiLiveList);
                    binding.txtTotalRequest.setText("" + requestMultiLiveList.size());
                    requestMultiLiveAdapter.notifyDataSetChanged();
                } else {


                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void setRequestListAdapter(List<GoLiveModelClass> requestMultiLiveList) {
        try {
            requestMultiLiveAdapter = new RequestMultiLiveAdapter(this, requestMultiLiveList, new RequestMultiLiveAdapter.Click() {
                @Override
                public void setOnRequestAcceptListener(GoLiveModelClass goLiveModelClass) {
                    requestListMultiBottomSheet.dismiss();
                    setPKRequestAcceptRejecte(goLiveModelClass, "1");
                }

                @Override
                public void setOnRequestRejectedListner(GoLiveModelClass goLiveModelClass) {
                    requestListMultiBottomSheet.dismiss();
                    setPKRequestAcceptRejecte(goLiveModelClass, "2");

                }

                @Override
                public void setOnRemoveUserListener(GoLiveModelClass goLiveModelClass) {
                    requestListMultiBottomSheet.dismiss();
                    setPKRequestAcceptRejecte(goLiveModelClass, "3");
                }
            });

        } catch (Exception e) {

        }
    }

    private void openRequestDialogForMultiLive(GoLiveModelClass goLiveModelClass) {

        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        DialogRequestMultiLiveBinding requestMultiLiveBinding = DialogRequestMultiLiveBinding.inflate(LayoutInflater.from(this));
        alert.setView(requestMultiLiveBinding.getRoot());
        alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            alertDialog.show();
            Glide.with(PKLiveActivity.this).load(goLiveModelClass.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(requestMultiLiveBinding.imgProfile);
        } catch (Exception e) {

        }

        requestMultiLiveBinding.txtUserName.setText(goLiveModelClass.getName() + " wants to join with you in live session.");

        requestMultiLiveBinding.btnAccept.setOnClickListener(view -> {
            alertDialog.dismiss();
            isPkConnected = true;
//            binding.viewTop.setVisibility(View.VISIBLE);
            Log.i("openReques", getChannelName + "  toke   " + getAccessToken);
            Log.i("openRequestDial", goLiveModelClass.getOtherUserChannelName() + "  toke   " + goLiveModelClass.getOtherUserToken());
            binding.rlMultiLiveRequest.setVisibility(View.GONE);
            ChannelMediaInfo channelMediaInfo = new ChannelMediaInfo(getChannelName, getAccessToken, 0);
            ChannelMediaRelayConfiguration mediaRelayConfiguration = new ChannelMediaRelayConfiguration();
            mediaRelayConfiguration.setSrcChannelInfo(channelMediaInfo);
            ChannelMediaInfo mediaInfo = new ChannelMediaInfo(goLiveModelClass.getOtherUserChannelName(), goLiveModelClass.getOtherUserToken(), 0);
            mediaRelayConfiguration.setDestChannelInfo(goLiveModelClass.getOtherUserChannelName(), mediaInfo);
            rtcEngine().startChannelMediaRelay(mediaRelayConfiguration);
            setPKRequestAcceptRejecte(goLiveModelClass, "1");

        });

        requestMultiLiveBinding.btnRejected.setOnClickListener(view -> {
            alertDialog.dismiss();
            setPKRequestAcceptRejecte(goLiveModelClass, "2");
        });

        requestMultiLiveBinding.imgClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

    }

    private void setPKRequestAcceptRejecte(GoLiveModelClass goLiveModelClass, String status) {
        goLiveModelClass.setRequestStatus(status);

        if (status.equalsIgnoreCase("1")) {

            binding.txtHostName.setText(name);
            try {
                Glide.with(PKLiveActivity.this).load(image).into(binding.imgHostImage);
            } catch (Exception e) {

            }


            final int minR = 0;
            final int maxR = 1000;
            final int randomR = new Random().nextInt((maxR - minR) + 1) + minR;

            SendPkGiftMode sendPkGiftMode = new SendPkGiftMode();
            sendPkGiftMode.setPkTotalCoin("0");
            sendPkGiftMode.setR(String.valueOf(randomR));
            ref.child(otherUserId).child(liveType).child(otherUserId).child("pkTotalCoinS").setValue(sendPkGiftMode);
            ref.child(otherUserId).child(liveType).child(otherUserId).child("receivedCoin").removeValue();


            Toast.makeText(PKLiveActivity.this, "" + goLiveModelClass.getImage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(PKLiveActivity.this, "" + goLiveModelClass.getName(), Toast.LENGTH_SHORT).show();

            Log.i("imagePath: ", "" + goLiveModelClass.getImage());
            Log.i("imagePath: ", "" + image);
            binding.txtOtherHostName.setText((goLiveModelClass.getName().equalsIgnoreCase("")) ? goLiveModelClass.getUserName() : goLiveModelClass.getName());
//                                binding.txtOtherHostName.setText("sdfghjk;lkhcxchkl");
            try {
                Glide.with(PKLiveActivity.this).load(goLiveModelClass.getImage()).into(binding.imgOtherHostImage);
            } catch (Exception e) {

            }

            pkUserId = goLiveModelClass.getUserID();
            ref.child(goLiveModelClass.getUserID()).child(liveType).child(goLiveModelClass.getUserID()).child("hostLiveInfo").child("pkStatus").setValue("2");
            ref.child(goLiveModelClass.getUserID()).child(liveType).child(goLiveModelClass.getUserID()).child("hostLiveInfo").child("otherUserChannelName").setValue(getChannelName);
            ref.child(goLiveModelClass.getUserID()).child(liveType).child(goLiveModelClass.getUserID()).child("hostLiveInfo").child("otherUserToken").setValue(getAccessToken);
            ref.child(goLiveModelClass.getUserID()).child(liveType).child(goLiveModelClass.getUserID()).child("hostLiveInfo").child("name").setValue(name);
            ref.child(goLiveModelClass.getUserID()).child(liveType).child(goLiveModelClass.getUserID()).child("hostLiveInfo").child("userName").setValue(CommonUtils.Companion.getUserName());

            ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("pkStatus").setValue("2");
            ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("otherUserChannelName").setValue(goLiveModelClass.getOtherUserChannelName());
            ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("otherUserToken").setValue(goLiveModelClass.getOtherUserToken());
            ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("name").setValue(goLiveModelClass.getName());
            ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("userName").setValue(goLiveModelClass.getUserName());


            GoLiveModelClass currentUserModel = new GoLiveModelClass();
            currentUserModel.setUserID(CommonUtils.Companion.getUserId());
            String image = NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getImage();
            currentUserModel.setImage(image);
            currentUserModel.setName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getName());
            currentUserModel.setUserName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getUsername());
            currentUserModel.setOtherUserToken(getAccessToken);
            currentUserModel.setOtherUserChannelName(getChannelName);
            currentUserModel.setPkStatus("2");
            currentUserModel.setRequestStatus("1");
            ref.child(goLiveModelClass.getUserID()).child(liveType).child(goLiveModelClass.getUserID()).child("PKLiveRequest").child(currentUserModel.getUserID()).setValue(currentUserModel);
            pkStartCount = pkStartCount + 1;
            pkEndCount = 1;

        }

    }

    private void updatePKBattleTime(String pkTime) {
        HashMap<String, String> data = new HashMap<>();
        data.put("pkTime", pkTime);
        ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("pkTime").setValue(pkTime.trim());

    }

    private void hitCreateLiveHistoryApi(String userId, String currentTime, String endLive, String liveType) {
        HashMap<String, String> data = new HashMap<>();
        data.put("userId", userId);
        data.put("startLive", currentTime);
        data.put("endLive", endLive);
        data.put("liveType", liveType);


        new VM().createLiveHistory(this, data).observe(this, new Observer<CreateLiveHistoryModel>() {
            @Override
            public void onChanged(CreateLiveHistoryModel createLiveHistoryModel) {
                if (createLiveHistoryModel.getSuccess().equalsIgnoreCase("1")) {
                    createLiveHistory = createLiveHistoryModel;
                }
                Toast.makeText(PKLiveActivity.this, "" + createLiveHistoryModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public static String getCurrentTime() {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }// en

    private void initHeartDrawables() {
        for (Integer i : drawableIds) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), i);
            Drawable drawable = new BitmapDrawable(bitmap);
            drawablesList.add(drawable);
        }
    }


    private void getHeart() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("flyingHeart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HeartModel data = snapshot.getValue(HeartModel.class);
                    int position = Integer.parseInt(data.getPosition());
                    binding.heartView.add(new HeartDrawable(drawablesList.get(position)));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void getMuteUsers() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("muteUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    muteUsers.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        GoLiveModelClass goLiveModelClass = snapshot1.getValue(GoLiveModelClass.class);
                        muteUsers.add(goLiveModelClass.getUserID());
                    }

                    if (liveStatus.equalsIgnoreCase("host")) {
                        if (muteUsers.contains(CommonUtils.Companion.getUserId())) {
                            Toast.makeText(PKLiveActivity.this, "You are muted by Host", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PKLiveActivity.this, "You are unMute by Host", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getBanListFirebase() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("banUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                banList.clear();
                list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        GoLiveModelClass goLiveModelClass = snapshot1.getValue(GoLiveModelClass.class);
                        banList.add(goLiveModelClass);
                        list.add(goLiveModelClass.getUserID());
                    }
                    checkUserBanedOrNot(list);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void checkUserBanedOrNot(List<String> banList) {
        for (int i = 0; i < banList.size(); i++) {
            if (banList.contains(CommonUtils.Companion.getUserId())) {
                Toast.makeText(this, "your are baned", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
//                Toast.makeText(this, "your are not baned", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void exitLiveStream() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("live").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean b = snapshot.getValue(Boolean.class);
                    if (!b) finish();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    // this function create viewer list
    private void firebaseAddDataUnderHostId() {
        GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
        goLiveModelClass.setUserID(CommonUtils.Companion.getUserId());
        String image = NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getImage();
        goLiveModelClass.setImage(image);
        goLiveModelClass.setName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getName());
        goLiveModelClass.setUserName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getUsername());
        goLiveModelClass.setLive(true);
        ref.child(otherUserId).child(liveType).child(otherUserId).child("viewer List").child(CommonUtils.Companion.getUserId()).setValue(goLiveModelClass);

    }

    private void sendMessage(ChatMessageModel chatMessageModel, String key) {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("chat comments").child(key).setValue(chatMessageModel);

    }


    @Override
    protected void onPause() {
        super.onPause();
        rtcEngine().disableVideo();
        rtcEngine().disableAudio();
        if (!liveStatus.equalsIgnoreCase("host")) {
            startService(new Intent(this, BackgroundService.class));
            Singleton.Companion.setStop(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        rtcEngine().enableAudio();
        rtcEngine().enableVideo();
        Log.i("onPause: ", "go on onResume");
        if (!liveStatus.equalsIgnoreCase("host")) {
            stopService(new Intent(this, BackgroundService.class));
            Singleton.Companion.setStop(false);
        }
        Log.i("onResume: ", String.valueOf(Singleton.Companion.isStopLive()));
        if (Singleton.Companion.isStopLive()) {
            Singleton.Companion.setStopLive(false);
            onBackPressed();
            stopService(new Intent(this, BackgroundService.class));
        }
    }

    private void hitEndLiveApi(String id, String currentTime) {
        HashMap<String, String> data = new HashMap<>();
        data.put("liveId", id);
        data.put("endLive", currentTime);

        new VM().endLive(this, data).observe(this, new Observer<CreateLiveHistoryModel>() {
            @Override
            public void onChanged(CreateLiveHistoryModel createLiveHistoryModel) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Singleton.Companion.setStopLive(false);
    }

    @Override
    protected void initUIandEvent() {

        if (liveStatus.equalsIgnoreCase("host")) {
            // if user is not host
            firebaseAddDataUnderHostId();
            sendJoinedMessage();
            if (liveType.equalsIgnoreCase("singleLive")) {
                binding.llOption.setVisibility(View.GONE);
                binding.rlGiftHeart.setVisibility(View.VISIBLE);
                binding.rlRequestMultiLive.setVisibility(View.GONE);
            } else {
                binding.llOption.setVisibility(View.GONE);
                binding.rlGiftHeart.setVisibility(View.VISIBLE);
                binding.rlRequestMultiLive.setVisibility(View.GONE);
                //multilive
            }
        } else {
// if user is host

            if (liveType.equalsIgnoreCase("singleLive")) {
                binding.rlMultiLiveRequest.setVisibility(View.GONE);
            } else {
                binding.rlMultiLiveRequest.setVisibility(View.VISIBLE);
            }
            getPKLiveRequestStatus();

            GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
            goLiveModelClass.setUserID(CommonUtils.Companion.getUserId());
            goLiveModelClass.setCurrentDiamond(CommonUtils.Companion.getCurrentDiamond());
            goLiveModelClass.setImage(CommonUtils.Companion.getImage());
            goLiveModelClass.setLiveStatus(liveStatus);
            goLiveModelClass.setLiveType(liveType);
            goLiveModelClass.setLive(true);
            goLiveModelClass.setPkStatus("1");
            goLiveModelClass.setName(CommonUtils.Companion.getName());
            goLiveModelClass.setUserName(CommonUtils.Companion.getUserName());

            ref.child(CommonUtils.Companion.getUserId()).child(liveType).child(CommonUtils.Companion.getUserId()).child("hostLiveInfo").setValue(goLiveModelClass);
            binding.llOption.setVisibility(View.VISIBLE);
            binding.rlGiftHeart.setVisibility(View.GONE);
            sendWelcomeMessageFirebase();
        }

        if (liveStatus.equalsIgnoreCase("host")) {
            exitLiveStream();
        }

        getCommentChatMessageFirebase();
        getViewerListFirebase();
        getHeart();
        getGift();
        getPkLiveStatus();
        getPkBattleCoinStatus();


        commentAdapter = new CommentAdapter(this, chatMessages);
        binding.recyclerAllMessage.setAdapter(commentAdapter);

        viewerAdapter = new ViewerAdapter(this, viewerList, liveStatus, new ViewerAdapter.Click() {
            @Override
            public void onBanned(int position) {
                openOtherUserProfile(viewerList.get(position));
//                setBannedUser(, position);
            }
        });
        binding.recyclerViewers.setAdapter(viewerAdapter);


        addEventHandler(this);

        final String encryptionKey = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY);
        final String encryptionMode = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE);

        doConfigEngine(encryptionKey, encryptionMode);

        mGridVideoViewContainer = findViewById(R.id.grid_video_view_container);
        mGridVideoViewContainer.setItemEventHandler(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                onBigVideoViewClicked(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemDoubleClick(View view, int position) {
//                onBigVideoViewDoubleClicked(view, position);
            }
        });

        if (!liveStatus.equalsIgnoreCase("host")) {
            SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
            preview(true, surfaceV, 0);
            surfaceV.setZOrderOnTop(false);
            surfaceV.setZOrderMediaOverlay(false);
            mUidsList.put(0, surfaceV); // get first surface view
            rtcEngine().enableLocalVideo(true);
            rtcEngine().enableLocalAudio(true);
            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

        } else {
            rtcEngine().enableLocalVideo(false);
            rtcEngine().enableLocalAudio(false);
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

        }

        mGridVideoViewContainer.initViewContainer(this, 0, mUidsList, mIsLandscape, mGridVideoViewContainer, RecyclerView.HORIZONTAL); // first is now full view

        joinChannel(getChannelName, config().mUid, getAccessToken);

        optional();


        binding.rlMuteVideo.setOnClickListener(view -> {
            onVideoMuteClicked(binding.imgVideoMute);
        });


        binding.rlMuteMic.setOnClickListener(view -> {
            onVoiceMuteClicked(binding.imgMuteMic);
        });


        binding.imgFlipCamera.setOnClickListener(view -> {
            onSwitchCameraClicked(binding.imgFlipCamera);
        });

        binding.rlFilter.setOnClickListener(view -> {
            onFilterClicked(binding.imgFilter);
        });


        binding.rlHeart.setOnClickListener(view -> {
            sendFlyingHeartInFirebase();
        });

        binding.rlSend.setOnClickListener(view -> {
            if (binding.edtMessage.getText().toString().trim().equalsIgnoreCase("")) {
            } else {
                if (muteUsers.contains(CommonUtils.Companion.getUserId())) {
                    Toast.makeText(this, "You can not send a message", Toast.LENGTH_SHORT).show();
                    binding.edtMessage.setText("");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String mess = "";
                            mess = binding.edtMessage.getText().toString();
                            binding.edtMessage.setText("");
                            sendCustomeMessage(mess, "");
                        }
                    });

                }
            }
        });

        binding.rlGift.setOnClickListener(view -> {
            openGiftDialog();

        });


        binding.rlRequestMultiLive.setOnClickListener(view -> {
            openPKBattleUserDialod();
        });


        binding.rlMultiLiveRequest.setOnClickListener(view -> {
            openRequestMultiLiveDialog();
        });

    }

    private String pkTotalCoin = "0";
    private String pkOtherUserTotalCoin = "0";
    Integer totalCoin = 0;
    SendPkGiftMode receivedCoin;

    private void getPkBattleCoinStatus() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("pkTotalCoinS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.i("onDataChange: ", "Pk other user id " + pkUserId);
                    receivedCoin = snapshot.getValue(SendPkGiftMode.class);
                    if (!receivedCoin.getPkTotalCoin().equalsIgnoreCase("")) {
                        pkTotalCoin = receivedCoin.getPkTotalCoin();
                        setCoinResultData();
                    }
                } else {
                    pkTotalCoin = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ref.child(pkUserId).child(liveType).child(pkUserId).child("pkTotalCoinS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Log.i("onDataChange: ", "Pk other user id " + pkUserId);
                if (snapshot.exists()) {
                    SendPkGiftMode receivedCoin = snapshot.getValue(SendPkGiftMode.class);
                    if (!receivedCoin.getPkTotalCoin().equalsIgnoreCase("")) {
                        pkOtherUserTotalCoin = receivedCoin.getPkTotalCoin();
                        setCoinResultData();

                    }

                } else {
                    pkOtherUserTotalCoin = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        if (!liveStatus.equalsIgnoreCase("host")) {

            ref.child(otherUserId).child(liveType).child(otherUserId).child("receivedCoin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        SendPkGiftMode receivedCoin = snapshot.getValue(SendPkGiftMode.class);
//                        Toast.makeText(PKLiveActivity.this, "pk total " + pkTotalCoin, Toast.LENGTH_SHORT).show();
                        try {
                            totalCoin = Integer.valueOf(pkTotalCoin) + Integer.valueOf(receivedCoin.getPkTotalCoin());
                        } catch (Exception e) {

                        }

                        final int minR = 0;
                        final int maxR = 1000;
                        final int randomR = new Random().nextInt((maxR - minR) + 1) + minR;

                        SendPkGiftMode sendPkGiftMode = new SendPkGiftMode();
                        sendPkGiftMode.setPkTotalCoin(String.valueOf(totalCoin));
                        sendPkGiftMode.setR(String.valueOf(randomR));
                        ref.child(otherUserId).child(liveType).child(otherUserId).child("pkTotalCoinS").setValue(sendPkGiftMode);
                    } else {
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }

    private boolean isHostShowWinner = false;
    private boolean isOtherHostShowWinner = false;

    private void setCoinResultData() {
        Log.i("setCoinResultData: ", pkOtherUserTotalCoin);
        Log.i("setCoinResultData: ", pkTotalCoin);
        binding.txtPkBattleCoin.setText(pkTotalCoin);
        binding.txtOtherUserPkCoin.setText(pkOtherUserTotalCoin);

        if (pkTotalCoin.equalsIgnoreCase(pkOtherUserTotalCoin)) {
            binding.progressBar.setProgressPercentage(50, true);
//            Toast.makeText(this, "equal", Toast.LENGTH_SHORT).show();
            Log.i("setCoinResultData: ", "equal coin");
        } else if (Integer.parseInt(pkTotalCoin) > Integer.parseInt(pkOtherUserTotalCoin)) {
//            Toast.makeText(this, "owner ", Toast.LENGTH_SHORT).show();
            Log.i("setCoinResultData: ", "host coin greater ");
//            binding.progressBar.setProgress(80);

            binding.progressBar.setProgressPercentage(80, true);
        } else {
            Log.i("setCoinResultData: ", "other host coin greater");
//            binding.progressBar.setProgress(20);
            binding.progressBar.setProgressPercentage(20, true);
        }


        if (Integer.parseInt(pkTotalCoin) > Integer.parseInt(pkOtherUserTotalCoin)) {
            binding.txtWinLooseHostName.setText("Win");
            binding.imgWinnLooseHostImage.setImageResource(R.drawable.win);
            binding.imgWinLooseOtherHostImage.setImageResource(R.drawable.loose);
            binding.txtWinLooseOtherHostName.setText("Loose");
            isHostShowWinner = true;
            isOtherHostShowWinner = false;
        } else if (Integer.parseInt(pkTotalCoin) == Integer.parseInt(pkOtherUserTotalCoin)) {
            binding.txtWinLooseHostName.setText("Tie");
            binding.txtWinLooseOtherHostName.setText("Tie");
            binding.imgWinnLooseHostImage.setImageResource(R.drawable.tie_left);
            binding.imgWinLooseOtherHostImage.setImageResource(R.drawable.tie_right);
            isHostShowWinner = false;
            isOtherHostShowWinner = false;
        } else {
            binding.txtWinLooseHostName.setText("Loose");
            binding.txtWinLooseOtherHostName.setText("Win");
            binding.imgWinnLooseHostImage.setImageResource(R.drawable.loose);
            binding.imgWinLooseOtherHostImage.setImageResource(R.drawable.win);
            isHostShowWinner = false;
            isOtherHostShowWinner = true;
        }


    }


    public float getPercentage(int value1, int value2) {
        float finalPercentage = 0;

        if (value1 > value2) {
            finalPercentage = (value1 / value2) * 100;
        } else {
            finalPercentage = (value2 / value1) * 100;
        }


        return finalPercentage;
    }

    private void openPKBattleUserDialod() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogPkLiveUsersBinding profileBottomSheetBinding = DialogPkLiveUsersBinding.inflate(LayoutInflater.from(this));
        bottomSheetDialog.setContentView(profileBottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        hitPkLiveUserApi(CommonUtils.Companion.getUserId(), bottomSheetDialog, profileBottomSheetBinding);


    }

    private void hitPkLiveUserApi(String userId, BottomSheetDialog bottomSheetDialog, DialogPkLiveUsersBinding profileBottomSheetBinding) {

        new VM().getPkLiveUser(this, userId).observe(this, new Observer<PKLiveUserModel>() {
            @Override
            public void onChanged(PKLiveUserModel pkLiveUserModel) {
                if (pkLiveUserModel.getSuccess().equalsIgnoreCase("1")) {
                    setPkLiveUserAdapter(pkLiveUserModel.getDimaond(), bottomSheetDialog, profileBottomSheetBinding);
                }
            }
        });


    }


    private void setPkLiveUserAdapter(List<Dimaond> dimaond, BottomSheetDialog bottomSheetDialog, DialogPkLiveUsersBinding profileBottomSheetBinding) {

        PKLiveUserAdapter pkLiveUserAdapter = new PKLiveUserAdapter(this, dimaond, new PKLiveUserAdapter.Click() {
            @Override
            public void setOnSendPKInvitationListener(Dimaond dimaond) {
                bottomSheetDialog.dismiss();
                sendRequestForMultiLive(dimaond);

            }
        });

        profileBottomSheetBinding.recyclerRequestMultiLive.setAdapter(pkLiveUserAdapter);

    }

    int pkStartCount = 1;
    int pkEndCount = 1;
    GoLiveModelClass pkOtherHostInfo;

    private void getPkLiveStatus() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pkOtherHostInfo = snapshot.getValue(GoLiveModelClass.class);

                    Log.i("onDataChange: ", "without check " + pkOtherHostInfo.getOtherUserChannelName() + " Token " + pkOtherHostInfo.getOtherUserToken());

                    if (pkOtherHostInfo.getPkStatus().equalsIgnoreCase("1")) {
                        if (pkEndCount == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!liveStatus.equalsIgnoreCase("host")) {

                                        binding.progressBar.setVisibility(View.GONE);
                                        binding.rlTime.setVisibility(View.GONE);
                                        binding.rlBothCoin.setVisibility(View.GONE);
                                        binding.llBothHost.setVisibility(View.GONE);
                                        binding.llWinLooseBothHost.setVisibility(View.GONE);
                                        pkUserId = "";
                                        pkStartCount = 1;
                                        pkEndCount++;
                                        isPkConnected = false;
                                        isAnimate = true;
                                        if (pkCountTime != null) {
                                            rtcEngine().stopChannelMediaRelay();
                                            pkCountTime.cancel();
                                        }
                                        binding.rlRequestMultiLive.setVisibility(View.VISIBLE);
                                        binding.rlMultiLiveRequest.setVisibility(View.VISIBLE);
                                        Toast.makeText(PKLiveActivity.this, "Other user left PK Battle", Toast.LENGTH_SHORT).show();

                                    } else {

                                    }
                                }
                            });

                        }
                    } else {
                        if (!isPkConnected) {
                            if (!pkOtherHostInfo.getOtherUserToken().equalsIgnoreCase("")) {
                                isPkConnected = true;
                                Toast.makeText(PKLiveActivity.this, "Please Wait while start pk battle", Toast.LENGTH_SHORT).show();
                                ChannelMediaInfo channelMediaInfo = new ChannelMediaInfo(getChannelName, getAccessToken, 0);
                                ChannelMediaRelayConfiguration mediaRelayConfiguration = new ChannelMediaRelayConfiguration();
                                mediaRelayConfiguration.setSrcChannelInfo(channelMediaInfo);
                                ChannelMediaInfo mediaInfo = new ChannelMediaInfo(pkOtherHostInfo.getOtherUserChannelName(), pkOtherHostInfo.getOtherUserToken(), 0);
                                mediaRelayConfiguration.setDestChannelInfo(pkOtherHostInfo.getOtherUserChannelName(), mediaInfo);
                                rtcEngine().startChannelMediaRelay(mediaRelayConfiguration);
                                pkStartCount = pkStartCount + 1;
                                pkEndCount = 1;
                                pkUserId = pkOtherHostInfo.getOtherUserChannelName();
                                ref.child(otherUserId).child(liveType).child(otherUserId).child("PKLiveRequest").child(pkOtherHostInfo.getOtherUserChannelName()).child("requestStatus").setValue("1");

                                Log.i("onDataChange: ", "firebase token " + pkOtherHostInfo.getOtherUserChannelName() + " Token " + pkOtherHostInfo.getOtherUserToken());
                                Log.i("onDataChange: ", getChannelName + " Token " + getAccessToken);
                            }


                        }
                        if (liveStatus.equalsIgnoreCase("host")) {
                            binding.txtPKTime.setText(pkOtherHostInfo.getPkTime());
                            if (pkOtherHostInfo.getPkTime().equalsIgnoreCase("00:30")) {
                                binding.llWinLooseBothHost.setVisibility(View.VISIBLE);
                            }

                            if (pkOtherHostInfo.getPkTime().equalsIgnoreCase("00:00")) {
                                openWinnerGifDialog();
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void hitSendGiftApi(String giftSenderId, String giftRecieverId, String amount, String giftId, String liveID, GiftsListModel.Detail detail) {
        HashMap<String, String> data = new HashMap<>();
        data.put("senderId", giftSenderId);
        data.put("diamond", amount);
        data.put("receiverId", giftRecieverId);
        data.put("giftId", giftId);
        data.put("liveId", liveID);
        new VM().sendGiftApi(this, data).observe(this, new Observer<GiftsListModel>() {
            @Override
            public void onChanged(GiftsListModel giftsListModel) {
                if (giftsListModel.getSuccess().equalsIgnoreCase("1")) {
                    sendGiftFirebase(detail);
                }
                Toast.makeText(PKLiveActivity.this, "" + giftsListModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendGiftFirebase(GiftsListModel.Detail detail) {

        GiftModel giftModel = new GiftModel();
        giftModel.setGiftPath(detail.getImage());
        giftModel.setGiftCoin(detail.getAmount());
        giftModel.setUserId(CommonUtils.Companion.getUserId());
        giftModel.setUserName(CommonUtils.Companion.getName());
        String key = ref.push().getKey();
        ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").child(key).setValue(giftModel);

        final int minR = 0;
        final int maxR = 1000;
        final int randomR = new Random().nextInt((maxR - minR) + 1) + minR;
        SendPkGiftMode sendPkGiftMode = new SendPkGiftMode();
        if (isPkConnected) {
            sendPkGiftMode.setPkTotalCoin(giftModel.getGiftCoin());
        } else {
            sendPkGiftMode.setPkTotalCoin("0");
        }
        sendPkGiftMode.setR(String.valueOf(randomR));
        ref.child(otherUserId).child(liveType).child(otherUserId).child("receivedCoin").setValue(sendPkGiftMode);


    }

    private void getPKLiveRequestStatus() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("PKLiveRequest").child(CommonUtils.Companion.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GoLiveModelClass goLiveModelClass = snapshot.getValue(GoLiveModelClass.class);
                    if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("1")) {
                        isLiveConnected = true;
                        Toast.makeText(PKLiveActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();
//                        setViewerGoLiveWithHost(goLiveModelClass);
                    } else if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("2")) {
                        isLiveConnected = false;
                        Toast.makeText(PKLiveActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    } else if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("3")) {
                        isLiveConnected = false;
                        removeByHostLive();
                        Toast.makeText(PKLiveActivity.this, "Remove By Host", Toast.LENGTH_SHORT).show();
                    } else {
                        isLiveConnected = false;
                        Toast.makeText(PKLiveActivity.this, "Waiting for host to accept request", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void removeByHostLive() {
        finish();
        deInitUIandEvent();

    }


    private void openRequestMultiLiveDialog() {
        requestListMultiBottomSheet = new BottomSheetDialog(this);
        DialogRequestMultiliveListBinding dialogGiftBinding = DialogRequestMultiliveListBinding.inflate(LayoutInflater.from(this));
        requestListMultiBottomSheet.setContentView(dialogGiftBinding.getRoot());
        dialogGiftBinding.recyclerRequestMultiLive.setAdapter(requestMultiLiveAdapter);
        requestListMultiBottomSheet.show();

    }

    private void sendRequestForMultiLive(Dimaond dimaond) {
        GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
        goLiveModelClass.setUserID(CommonUtils.Companion.getUserId());
        String image = NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getImage();
        goLiveModelClass.setImage(image);
        goLiveModelClass.setName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getName());
        goLiveModelClass.setUserName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getUsername());
        goLiveModelClass.setOtherUserToken(getAccessToken);
        goLiveModelClass.setOtherUserChannelName(getChannelName);
        goLiveModelClass.setPkStatus("1");
        goLiveModelClass.setRequestStatus("");
        ref.child(dimaond.getUserId()).child(liveType).child(dimaond.getUserId()).child("PKLiveRequest").child(CommonUtils.Companion.getUserId()).setValue(goLiveModelClass);
        pkUserId = dimaond.getUserId();
    }


    private void openGiftDialog() {
        final BottomSheetDialog giftBottomDialogSheet = new BottomSheetDialog(this);
        DialogGiftBinding dialogGiftBinding = DialogGiftBinding.inflate(LayoutInflater.from(this));
        giftBottomDialogSheet.setContentView(dialogGiftBinding.getRoot());
        giftBottomDialogSheet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        HashMap<String, String> data = new HashMap<>();
        data.put("userId", CommonUtils.Companion.getUserId());

        new VM().getGifts(this, data).observe(this, new Observer<GiftsListModel>() {
            @Override
            public void onChanged(GiftsListModel giftsListModel) {
                if (giftsListModel.getSuccess().equalsIgnoreCase("1")) {
                    setGiftListAdapter(giftsListModel.getDetails(), dialogGiftBinding, giftBottomDialogSheet);
                    if (!giftsListModel.getDimaond().equalsIgnoreCase("")) {
                        dialogGiftBinding.txtTotalDiamond.setText(CommonUtils.Companion.prettyCount(Long.valueOf(giftsListModel.getDimaond())));

                    } else {
                        dialogGiftBinding.txtTotalDiamond.setText("00.0");
                    }

                }
                Toast.makeText(PKLiveActivity.this, "" + giftsListModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        giftBottomDialogSheet.show();

    }

    private void setGiftListAdapter(List<GiftsListModel.Detail> details, DialogGiftBinding dialogGiftBinding, BottomSheetDialog giftBottomDialogSheet) {

        GiftAdapter giftAdapter = new GiftAdapter(this, details, new GiftAdapter.Click() {
            @Override
            public void setOnSendGiftListener(GiftsListModel.Detail detail) {
//                if (liveType.equalsIgnoreCase("singleLive")) {
////                    sendGiftFirebase(detail, new GoLiveModelClass());
//                    hitSendGiftApi(CommonUtils.Companion.getUserId(), otherUserId, detail.getAmount(), detail.getId(), liveId, detail);
//
//                } else {
////                    if (liveJoinedUserList.size() <= 1) {
////                        sendGiftFirebase(detail, new GoLiveModelClass());
////                    } else {
////                        openDialogUsersLiveJoined(detail);
////                    }
//
//                    hitSendGiftApi(CommonUtils.Companion.getUserId(), otherUserId, detail.getAmount(), detail.getId(), liveId, detail);
//
//                }

                hitSendGiftApi(CommonUtils.Companion.getUserId(), otherUserId, detail.getAmount(), detail.getId(), liveId, detail);
                giftBottomDialogSheet.dismiss();

            }
        });
        dialogGiftBinding.recyclerGift.setAdapter(giftAdapter);
    }

    UserJoinedAdapter userJoinedAdapter;

    private void openDialogUsersLiveJoined(GiftsListModel.Detail detail) {
        BottomSheetDialog sendGiftUsersDialog = new BottomSheetDialog(this);
        DialogLiveUserJoinedBinding liveUserJoinedBinding = DialogLiveUserJoinedBinding.inflate(LayoutInflater.from(this));
        sendGiftUsersDialog.setContentView(liveUserJoinedBinding.getRoot());
        userJoinedAdapter = new UserJoinedAdapter(this, liveJoinedUserList, new UserJoinedAdapter.Click() {
            @Override
            public void setOnSendGiftListener(GoLiveModelClass goLiveModelClass) {
                sendGiftUsersDialog.dismiss();
//                sendGiftFirebase(detail, goLiveModelClass);
            }
        });

        liveUserJoinedBinding.recyclerRequestMultiLive.setAdapter(userJoinedAdapter);
        sendGiftUsersDialog.show();
    }

    private void sendFlyingHeartInFirebase() {
//for position
        int size = drawableIds.length;
        final int min = 0;
        final int max = size - 1;
        final int random = new Random().nextInt((max - min) + 1) + min;

//for update listner
        final int minR = 0;
        final int maxR = 1000;
        final int randomR = new Random().nextInt((maxR - minR) + 1) + minR;

        HeartModel heartModel = new HeartModel();
        heartModel.setPosition(String.valueOf(random));
        heartModel.setRandom(String.valueOf(randomR));
        ref.child(otherUserId).child(liveType).child(otherUserId).child("flyingHeart").setValue(heartModel);

    }

    private void openOtherUserProfile(@NotNull GoLiveModelClass goLiveModelClass) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        ProfileBottomSheetBinding profileBottomSheetBinding = ProfileBottomSheetBinding.inflate(LayoutInflater.from(this));
        bottomSheetDialog.setContentView(profileBottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        profileBottomSheetBinding.txtName.setText(goLiveModelClass.getName());
        profileBottomSheetBinding.txtUserName.setText(goLiveModelClass.getUserName());

        if (muteUsers.contains(goLiveModelClass.getUserID())) {
            profileBottomSheetBinding.txtMute.setText("UnMute");
        } else {
            profileBottomSheetBinding.txtMute.setText("Mute");
        }


        Glide.with(this).load(goLiveModelClass.getImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                profileBottomSheetBinding.progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                profileBottomSheetBinding.progress.setVisibility(View.GONE);
                return false;
            }
        }).into(profileBottomSheetBinding.imgProfile);

        profileBottomSheetBinding.llMute.setOnClickListener(view -> {
            if (muteUsers.contains(goLiveModelClass.getUserID())) {
                GoLiveModelClass goLiveModelClass1 = new GoLiveModelClass();
                goLiveModelClass1.setUserID("-1");
                setMuteUnMuteUser(goLiveModelClass1, goLiveModelClass.getUserID());
            } else {
                setMuteUnMuteUser(goLiveModelClass, goLiveModelClass.getUserID());
            }
            bottomSheetDialog.dismiss();
        });

        profileBottomSheetBinding.llBan.setOnClickListener(view -> {
            setBannedUser(goLiveModelClass);
            bottomSheetDialog.dismiss();
        });

        profileBottomSheetBinding.llProfile.setOnClickListener(view -> {
            Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();

        });

        profileBottomSheetBinding.llBlock.setOnClickListener(view -> {
            Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();

        });


    }

    private void setMuteUnMuteUser(GoLiveModelClass goLiveModelClass, String userId) {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("muteUsers").child(userId).setValue(goLiveModelClass);
    }

    private void setBannedUser(GoLiveModelClass goLiveModelClass) {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("banUser").child(goLiveModelClass.getUserID()).setValue(goLiveModelClass);
    }

    private void getViewerListFirebase() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("viewer List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    viewerList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GoLiveModelClass goLiveModelClass = dataSnapshot.getValue(GoLiveModelClass.class);

                        if (goLiveModelClass.isLive()) {
                            viewerList.add(goLiveModelClass);
                        }


                    }
                    viewerAdapter.notifyDataSetChanged();
                    binding.recyclerViewers.scrollToPosition(viewerList.size() - 1);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendCustomeMessage(String mess, String gift) {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setGift(gift);
        chatMessageModel.setImage(CommonUtils.Companion.getImage());
        chatMessageModel.setKey(ref.push().getKey());
        chatMessageModel.setMessage(mess);
        chatMessageModel.setName(CommonUtils.Companion.getName());
        chatMessageModel.setUserId(CommonUtils.Companion.getUserId());
        sendMessage(chatMessageModel, chatMessageModel.getKey());
    }

    private void sendJoinedMessage() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setGift("");
        chatMessageModel.setImage(CommonUtils.Companion.getImage());
        chatMessageModel.setKey(ref.push().getKey());
        chatMessageModel.setMessage("joined Stream");
        chatMessageModel.setName(CommonUtils.Companion.getName());
        chatMessageModel.setUserId(CommonUtils.Companion.getUserId());
        sendMessage(chatMessageModel, chatMessageModel.getKey());
    }

    private void getCommentChatMessageFirebase() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("chat comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatMessages.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatMessageModel chatMessageModel = dataSnapshot.getValue(ChatMessageModel.class);
                        chatMessages.add(chatMessageModel);
                    }

                    commentAdapter.notifyDataSetChanged();
                    binding.recyclerAllMessage.scrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendWelcomeMessageFirebase() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setGift("");
        chatMessageModel.setImage(CommonUtils.Companion.getImage());
        chatMessageModel.setKey(ref.push().getKey());
        chatMessageModel.setMessage("Welcome to pretty live stream");
        chatMessageModel.setName(CommonUtils.Companion.getName());
        chatMessageModel.setUserId(CommonUtils.Companion.getUserId());
        sendMessage(chatMessageModel, chatMessageModel.getKey());
    }


    private void deleterLiveStreamViewers() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("viewer List").child(CommonUtils.Companion.getUserId()).child("live").setValue(false);
        sendCustomeMessage("Left stream", "");
    }

    private void setLiveStreamOffline() {
        HashMap<String, Boolean> data = new HashMap<>();
        data.put("live", false);
        ref.child(CommonUtils.Companion.getUserId()).child(liveType).child(CommonUtils.Companion.getUserId()).child("hostLiveInfo").setValue(data);

    }

    private void optional() {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private void optionalDestroy() {
    }

    private int getVideoEncResolutionIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncResolutionIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX);
        if (videoEncResolutionIndex > ConstantApp.VIDEO_DIMENSIONS.length - 1) {
            videoEncResolutionIndex = ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, videoEncResolutionIndex);
            editor.apply();
        }
        return videoEncResolutionIndex;
    }

    private int getVideoEncFpsIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncFpsIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX);
        if (videoEncFpsIndex > ConstantApp.VIDEO_FPS.length - 1) {
            videoEncFpsIndex = ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, videoEncFpsIndex);
            editor.apply();
        }
        return videoEncFpsIndex;
    }

    private void doConfigEngine(String encryptionKey, String encryptionMode) {
        VideoEncoderConfiguration.VideoDimensions videoDimension = ConstantApp.VIDEO_DIMENSIONS[4];
        VideoEncoderConfiguration.FRAME_RATE videoFps = ConstantApp.VIDEO_FPS[getVideoEncFpsIndex()];
        configEngine(videoDimension, videoFps, encryptionKey, encryptionMode);
    }

    public void onSwitchCameraClicked(View view) {
        RtcEngine rtcEngine = rtcEngine();
        // Switches between front and rear cameras.
        rtcEngine.switchCamera();
    }


    public void onFilterClicked(View view) {
        Constant.BEAUTY_EFFECT_ENABLED = !Constant.BEAUTY_EFFECT_ENABLED;

        if (Constant.BEAUTY_EFFECT_ENABLED) {
            setBeautyEffectParameters(Constant.BEAUTY_EFFECT_DEFAULT_LIGHTNESS, Constant.BEAUTY_EFFECT_DEFAULT_SMOOTHNESS, Constant.BEAUTY_EFFECT_DEFAULT_REDNESS);
            enablePreProcessor();
        } else {
            disablePreProcessor();
        }

        ImageView iv = (ImageView) view;

//        iv.setImageResource(Constant.BEAUTY_EFFECT_ENABLED ? R.drawable.btn_filter : R.drawable.btn_filter_off);
    }

    @Override
    protected void deInitUIandEvent() {
        if (!liveStatus.equalsIgnoreCase("host")) {
            setLiveStreamOffline();
            hitEndLiveApi(createLiveHistory.getDetails().getId(), getCurrentTime());
            ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("pkStatus").setValue("1");
            ref.child(pkUserId).child(liveType).child(pkUserId).child("hostLiveInfo").child("pkStatus").setValue("1");
            ref.child(pkUserId).child(liveType).child(pkUserId).child("PKLiveRequest").child(CommonUtils.Companion.getUserId()).child("requestStatus").setValue("2");
            if (pkCountTime != null) {
                rtcEngine().stopChannelMediaRelay();
                pkCountTime.cancel();
            }
            ref.child(CommonUtils.Companion.getUserId()).child(liveType).removeValue();

        }

        if (isLiveConnected) {
            setViewerExitInMultiLive();
        }

        deleterLiveStreamViewers();
        optionalDestroy();
        doLeaveChannel();
        removeEventHandler(this);
        mUidsList.clear();

    }

    private void setViewerExitInMultiLive() {
        GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
        goLiveModelClass.setUserID(CommonUtils.Companion.getUserId());
        String image = NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getImage();
        goLiveModelClass.setImage(image);
        goLiveModelClass.setName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getName());
        goLiveModelClass.setUserName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getUsername());
        goLiveModelClass.setRequestStatus("2");
        setPKRequestAcceptRejecte(goLiveModelClass, "2");
    }

    private void doLeaveChannel() {
        leaveChannel(config().mChannel);
        preview(false, null, 0);
    }


    public void onVideoMuteClicked(View view) {
        log.info("onVoiceChatClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size() == 0) {
            return;
        }

        SurfaceView surfaceV = getLocalView();
        ViewParent parent;
        if (surfaceV == null || (parent = surfaceV.getParent()) == null) {
            log.warn("onVoiceChatClicked " + view + " " + surfaceV);
            return;
        }

        RtcEngine rtcEngine = rtcEngine();
        mVideoMuted = !mVideoMuted;

        if (mVideoMuted) {
            rtcEngine.enableLocalVideo(false);
        } else {
            rtcEngine.enableLocalVideo(true);
        }

        ImageView iv = (ImageView) view;

        iv.setImageResource(mVideoMuted ? R.drawable.ic_baseline_videocam_off_24 : R.drawable.ic_baseline_videocam_24);

//        hideLocalView(mVideoMuted);
    }

    private SurfaceView getLocalView() {
        for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == config().mUid) {
                return entry.getValue();
            }
        }

        return null;
    }


    public void onVoiceMuteClicked(View view) {
        log.info("onVoiceMuteClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size() == 0) {
            return;
        }
        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);
        ImageView iv = (ImageView) view;
        iv.setImageResource(mAudioMuted ? R.drawable.ic_baseline_mic_off_24 : R.drawable.ic_baseline_mic_24);
    }

    public void onMixingAudioClicked(View view) {
        log.info("onMixingAudioClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted + " mixing_audio: " + mMixingAudio);

        if (mUidsList.size() == 0) {
            return;
        }

        mMixingAudio = !mMixingAudio;

        RtcEngine rtcEngine = rtcEngine();
        if (mMixingAudio) {
            rtcEngine.startAudioMixing(Constant.MIX_FILE_PATH, false, false, -1);
        } else {
            rtcEngine.stopAudioMixing();
        }

        ImageView iv = (ImageView) view;
//        iv.setImageResource(mMixingAudio ? R.drawable.btn_audio_mixing : R.drawable.btn_audio_mixing_off);
    }

    @Override
    public void onUserJoined(int uid) {
        log.debug("onUserJoined " + (uid & 0xFFFFFFFFL));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                notifyMessageChanged(new Message(new User(0, null), "user " + (uid & 0xFFFFFFFFL) + " joined"));
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);

    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    return;
                }

                /*
                  Creates the video renderer view.
                  CreateRendererView returns the SurfaceView type. The operation and layout of the
                  view are managed by the app, and the Agora SDK renders the view provided by the
                  app. The video display view must be created using this method instead of
                  directly calling SurfaceView.
                 */
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mUidsList.put(uid, surfaceV);

                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;

                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);

                /*
                  Initializes the video view of a remote user.
                  This method initializes the video view of a remote stream on the local device. It affects only the video view that the local user sees.
                  Call this method to bind the remote video stream to a video view and to set the rendering and mirror modes of the video view.
                 */


                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                if (useDefaultLayout) {
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(bigBgUid);
                }

//                notifyMessageChanged(new Message(new User(0, null), "video from user " + (uid & 0xFFFFFFFFL) + " decoded"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Log.i("onRemoteVide", "" + uid + "  " + state + "  ");
                        if (isAnimate) {
                            if (mUidsList.size() == 2) {
                                isAnimate = false;

                                binding.txtHostName.setText(name);
                                try {
                                    Glide.with(PKLiveActivity.this).load(image).into(binding.imgHostImage);
                                } catch (Exception e) {

                                }

//                                Log.i("runUserName: ", "" + pkOtherHostInfo.getUserName());
//                                Log.i("runUserName: ", "" + pkOtherHostInfo.getName());
                                binding.txtOtherHostName.setText((pkOtherHostInfo.getName().equalsIgnoreCase("")) ? pkOtherHostInfo.getUserName() : pkOtherHostInfo.getName());
                                try {
                                    Glide.with(PKLiveActivity.this).load(pkOtherHostInfo.getImage()).into(binding.imgOtherHostImage);
                                } catch (Exception e) {

                                }


                                binding.llBothHost.setVisibility(View.VISIBLE);
                                binding.rlHostInfo.setVisibility(View.VISIBLE);
                                Animation hostOwenerAnimation = AnimationUtils.loadAnimation(PKLiveActivity.this, R.anim.left_to_right);
                                binding.rlHostInfo.startAnimation(hostOwenerAnimation);
                                binding.rlOtherHostInfo.setVisibility(View.VISIBLE);
                                Animation hostOtherHostAnimation = AnimationUtils.loadAnimation(PKLiveActivity.this, R.anim.right_to_left);
                                binding.rlOtherHostInfo.startAnimation(hostOtherHostAnimation);


                                CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
                                    @Override
                                    public void onTick(long l) {

                                    }

                                    @Override
                                    public void onFinish() {

                                        Animation hostOwenerAnimation = AnimationUtils.loadAnimation(PKLiveActivity.this, R.anim.left_to_right_gone);
                                        binding.rlHostInfo.startAnimation(hostOwenerAnimation);
                                        binding.rlHostInfo.setVisibility(View.GONE);

                                        Animation hostOtherHostAnimation = AnimationUtils.loadAnimation(PKLiveActivity.this, R.anim.right_to_left_gone);
                                        binding.rlOtherHostInfo.startAnimation(hostOtherHostAnimation);
                                        binding.rlOtherHostInfo.setVisibility(View.GONE);
                                    }
                                };

                                countDownTimer.start();


                                binding.progressBar.setVisibility(View.VISIBLE);
                                binding.rlTime.setVisibility(View.VISIBLE);
                                binding.rlBothCoin.setVisibility(View.VISIBLE);
                                binding.rlRequestMultiLive.setVisibility(View.GONE);
                                binding.rlMultiLiveRequest.setVisibility(View.GONE);
                                //here channel name is also other user id
                                getPkBattleCoinStatus();

                                if (!liveStatus.equalsIgnoreCase("host")) {
                                    if (pkCountTime != null) {
                                        pkCountTime.cancel();
                                    }

                                    final int minR = 0;
                                    final int maxR = 1000;
                                    final int randomR = new Random().nextInt((maxR - minR) + 1) + minR;

                                    SendPkGiftMode sendPkGiftMode = new SendPkGiftMode();
                                    sendPkGiftMode.setPkTotalCoin("0");
                                    sendPkGiftMode.setR(String.valueOf(randomR));
                                    ref.child(otherUserId).child(liveType).child(otherUserId).child("pkTotalCoinS").setValue(sendPkGiftMode);
                                    ref.child(otherUserId).child(liveType).child(otherUserId).child("receivedCoin").removeValue();


                                    pkCountTime = new CountDownTimer(100000, 1000) {
                                        @Override
                                        public void onTick(long l) {
                                            binding.txtPKTime.setText("" + String.format("%02d:%02d ",
                                                    TimeUnit.MILLISECONDS.toMinutes(l),
                                                    TimeUnit.MILLISECONDS.toSeconds(l) -
                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
                                            updatePKBattleTime(binding.txtPKTime.getText().toString());

                                            if (pkOtherHostInfo.getPkTime().equalsIgnoreCase("00:30")) {
                                                binding.llWinLooseBothHost.setVisibility(View.VISIBLE);
                                            }


                                        }

                                        @Override
                                        public void onFinish() {
                                            openWinnerGifDialog();
//                                            Glide.with(PKLiveActivity.this).load(R.drawable.animation).into(binding.lottieGift);
//                                            binding.lottieGift.setVisibility(View.VISIBLE);

                                            Toast.makeText(PKLiveActivity.this, "Finished Pk Battle", Toast.LENGTH_SHORT).show();

                                        }
                                    };

                                    pkCountTime.start();
                                }


                            }
                        }
                    }
                });

            }
        });
    }

    private void openWinnerGifDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        DialogShowGiftBinding requestMultiLiveBinding = DialogShowGiftBinding.inflate(LayoutInflater.from(this));
        alert.setView(requestMultiLiveBinding.getRoot());
        alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            alertDialog.show();
            Glide.with(PKLiveActivity.this).load(R.drawable.animation_two).into(requestMultiLiveBinding.imgGift);
        } catch (Exception e) {
        }


        if (isHostShowWinner) {
            requestMultiLiveBinding.imgWinner.setVisibility(View.VISIBLE);
            Glide.with(PKLiveActivity.this).load(R.drawable.winner).into(requestMultiLiveBinding.imgWinner);
        } else {
            requestMultiLiveBinding.imgWinner.setVisibility(View.GONE);
        }


        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.i("onTick: ", String.format("%02d:%02d ",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
                pkStartCount = 1;
                pkEndCount++;
                isPkConnected = false;
                isAnimate = true;
                if (pkCountTime != null) {
                    rtcEngine().stopChannelMediaRelay();
                    pkCountTime.cancel();
                }
                if (!liveStatus.equalsIgnoreCase("host")) {
                    binding.rlRequestMultiLive.setVisibility(View.VISIBLE);
                    binding.rlMultiLiveRequest.setVisibility(View.VISIBLE);
                    ref.child(pkUserId).child(liveType).child(pkUserId).child("hostLiveInfo").child("pkStatus").setValue("1");
                    ref.child(pkUserId).child(liveType).child(pkUserId).child("hostLiveInfo").child("otherUserChannelName").setValue("");
                    ref.child(pkUserId).child(liveType).child(pkUserId).child("hostLiveInfo").child("otherUserToken").setValue("");
                    ref.child(pkUserId).child(liveType).child(pkUserId).child("hostLiveInfo").child("name").setValue("");
                    ref.child(pkUserId).child(liveType).child(pkUserId).child("hostLiveInfo").child("userName").setValue("");

                    ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("pkStatus").setValue("1");
                    ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("otherUserChannelName").setValue("");
                    ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("otherUserToken").setValue("");
                    ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("name").setValue("");
                    ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").child("userName").setValue("");

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("requestStatus", "2");
                    ref.child(otherUserId).child(liveType).child(otherUserId).child("PKLiveRequest").child(pkUserId).updateChildren(data);
                }
                pkUserId = "";

            }
        };
        countDownTimer.start();


    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        log.debug("onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);

        doRemoveRemoteUi(uid);
        isAnimate = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if (mUidsList.size() < 2) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.rlTime.setVisibility(View.GONE);
                    binding.rlBothCoin.setVisibility(View.GONE);
                    binding.llBothHost.setVisibility(View.GONE);
                    binding.llWinLooseBothHost.setVisibility(View.GONE);
                }


                if (!liveStatus.equalsIgnoreCase("host")) {
                    if (otherUserId.equalsIgnoreCase(pkUserId)) {
                        pkStartCount = 1;
                        pkEndCount++;
                        isPkConnected = false;
                        pkUserId = "";
                        binding.progressBar.setVisibility(View.GONE);
                        binding.rlTime.setVisibility(View.GONE);
                        binding.llWinLooseBothHost.setVisibility(View.GONE);
                        binding.rlBothCoin.setVisibility(View.GONE);
                        binding.llBothHost.setVisibility(View.GONE);
                        if (pkCountTime != null) {
                            rtcEngine().stopChannelMediaRelay();
                            Log.i("runStopRelay: ", "");
                            pkCountTime.cancel();
                        }


                        binding.rlRequestMultiLive.setVisibility(View.VISIBLE);
                        binding.rlMultiLiveRequest.setVisibility(View.VISIBLE);
                    } else {

                    }
                }
            }
        });
    }

    @Override
    public void onExtraCallback(final int type, final Object... data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                doHandleExtraCallback(type, data);
            }
        });
    }

    private boolean isAnimate = true;

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

        if (state == 0) {
        } else {
            doRenderRemoteUi(uid);

        }
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> status = new HashMap<>();
                    status.put(peerUid, muted ? UserStatusData.AUDIO_MUTED : UserStatusData.DEFAULT_STATUS);
                    mGridVideoViewContainer.notifyUiChanged(mUidsList, config().mUid, status, null);
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];
//                doHideTargetView(peerUid, muted);

                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_STATS:
                IRtcEngineEventHandler.RemoteVideoStats stats = (IRtcEngineEventHandler.RemoteVideoStats) data[0];

                if (Constant.SHOW_VIDEO_INFO) {
                    if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                        mGridVideoViewContainer.addVideoInfo(stats.uid, new VideoInfoData(stats.width, stats.height, stats.delay, stats.rendererOutputFrameRate, stats.receivedBitrate));
                        int uid = config().mUid;
                        int profileIndex = getVideoEncResolutionIndex();
                        String resolution = getResources().getStringArray(R.array.string_array_resolutions)[profileIndex];
                        String fps = getResources().getStringArray(R.array.string_array_frame_rate)[profileIndex];

                        String[] rwh = resolution.split("x");
                        int width = Integer.valueOf(rwh[0]);
                        int height = Integer.valueOf(rwh[1]);

                        mGridVideoViewContainer.addVideoInfo(uid, new VideoInfoData(width > height ? width : height,
                                width > height ? height : width,
                                0, Integer.valueOf(fps), Integer.valueOf(0)));
                    }
                } else {
                    mGridVideoViewContainer.cleanVideoInfo();
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS:
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];

                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> volume = new HashMap<>();

                    for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                        peerUid = each.uid;
                        int peerVolume = each.volume;

                        if (peerUid == 0) {
                            continue;
                        }
                        volume.put(peerUid, peerVolume);
                    }
                    mGridVideoViewContainer.notifyUiChanged(mUidsList, config().mUid, null, volume);
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_APP_ERROR:
                int subType = (int) data[0];

                if (subType == ConstantApp.AppError.NO_CONNECTION_ERROR) {
                    String msg = getString(R.string.msg_connection_error);
//                    notifyMessageChanged(new Message(new User(0, null), msg));
                    showLongToast(msg);
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG:

                peerUid = (Integer) data[0];
                final byte[] content = (byte[]) data[1];
//                notifyMessageChanged(new Message(new User(peerUid, String.valueOf(peerUid)), new String(content)));

                break;

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];

//                notifyMessageChanged(new Message(new User(0, null), error + " " + description));

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED:
                notifyHeadsetPlugged((int) data[0]);

                break;

        }
    }

    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                binding.viewTop.setVisibility(View.GONE);
                if (isFinishing()) {
                    return;
                }

                Object target = mUidsList.remove(uid);
                if (target == null) {
                    return;
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL) + " " + mLayoutType);

                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {


                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }

//                notifyMessageChanged(new Message(new User(0, null), "user " + (uid & 0xFFFFFFFFL) + " left"));
            }
        });
    }

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock.setVisibility(View.GONE);
        }
        mGridVideoViewContainer.initViewContainer(this, config().mUid, mUidsList, mIsLandscape, mGridVideoViewContainer, RecyclerView.HORIZONTAL);

        mLayoutType = LAYOUT_TYPE_DEFAULT;
        boolean setRemoteUserPriorityFlag = false;
        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                if (!setRemoteUserPriorityFlag) {
                    setRemoteUserPriorityFlag = true;
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH);
                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                } else {
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORMAL);
                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                }
            }
        }
    }

    private void switchToSmallVideoView(int bigBgUid) {
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(bigBgUid, mUidsList.get(bigBgUid));
        Iterator<SurfaceView> iterator = mUidsList.values().iterator();
        while (iterator.hasNext()) {
            SurfaceView s = iterator.next();
            s.setZOrderOnTop(true);
            s.setZOrderMediaOverlay(true);
        }

        mUidsList.get(bigBgUid).setZOrderOnTop(false);
        mUidsList.get(bigBgUid).setZOrderMediaOverlay(false);

        mGridVideoViewContainer.initViewContainer(this, bigBgUid, slice, mIsLandscape, mGridVideoViewContainer, RecyclerView.HORIZONTAL);

        bindToSmallVideoView(bigBgUid);

        mLayoutType = LAYOUT_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        boolean twoWayVideoCall = mUidsList.size() == 2;

        RecyclerView recycler = findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, config().mUid, exceptUid, mUidsList, binding.gridVideoViewContainer);
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        log.debug("bindToSmallVideoView " + twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));

        if (twoWayVideoCall) {
            recycler.setLayoutManager(new RtlLinearLayoutManager(getApplicationContext(), RtlLinearLayoutManager.HORIZONTAL, false));
        } else {
            recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        recycler.addItemDecoration(new SmallVideoViewDecoration());
        recycler.setAdapter(mSmallVideoViewAdapter);
        recycler.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemDoubleClick(View view, int position) {
                //onSmallVideoViewDoubleClicked(view, position);
            }
        }));

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.setLocalUid(config().mUid);
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        for (Integer tempUid : mUidsList.keySet()) {
            if (config().mUid != tempUid) {
                if (tempUid == exceptUid) {
                    rtcEngine().setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_HIGH);
                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                } else {
                    rtcEngine().setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_NORMAL);
                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                }
            }
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }

    public void notifyHeadsetPlugged(final int routing) {
        log.info("notifyHeadsetPlugged " + routing + " " + mVideoMuted);

        mAudioRouting = routing;

        ImageView iv = findViewById(R.id.switch_speaker_id);
//        if (mAudioRouting == Constants.AUDIO_ROUTE_SPEAKERPHONE) {
//            iv.setImageResource(R.drawable.btn_speaker);
//        } else {
//            iv.setImageResource(R.drawable.btn_speaker_off);
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
            switchToDefaultVideoView();
        } else if (mSmallVideoViewAdapter != null) {
            switchToSmallVideoView(mSmallVideoViewAdapter.getExceptedUid());
        }
    }
}
