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
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import com.mobile.donalive.agora.firebase.RequestMultiLiveAdapter;
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
import com.mobile.donalive.databinding.ActivityCallBinding;
import com.mobile.donalive.databinding.DialogGiftBinding;
import com.mobile.donalive.databinding.DialogLiveUserJoinedBinding;
import com.mobile.donalive.databinding.DialogReceivedGiftBinding;
import com.mobile.donalive.databinding.DialogRequestMultiLiveBinding;
import com.mobile.donalive.databinding.DialogRequestMultiliveListBinding;
import com.mobile.donalive.databinding.ProfileBottomSheetBinding;
import com.mobile.donalive.modelclass.CreateLiveHistoryModel;
import com.mobile.donalive.modelclass.Details;
import com.mobile.donalive.modelclass.GoLiveModelClass;
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

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static com.mobile.donalive.agora.openvcall.model.ConstantApp.VIDEO_DIMENSIONS;


public class CallActivity extends BaseActivity implements DuringCallEventHandler {

    public static final int LAYOUT_TYPE_DEFAULT = 0;
    public static final int LAYOUT_TYPE_SMALL = 1;
    private final static Logger log = LoggerFactory.getLogger(CallActivity.class);
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

    private ActivityCallBinding binding;
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
    private String liveId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(LayoutInflater.from(this));
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
            ref.child(CommonUtils.Companion.getUserId()).child(liveType).removeValue();
            hitCreateLiveHistoryApi(CommonUtils.Companion.getUserId(), getCurrentTime(), "", liveType);
            getMultiLiveRequest();
        } else {
            liveId = getIntent().getStringExtra("liveId");
        }
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
            Glide.with(CallActivity.this).load(giftModel.getGiftPath()).into(dialogReceivedGiftBinding.imgShowGif);
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


    private void getGift() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.exists()) {
                    GiftModel giftModel = snapshot.getValue(GiftModel.class);

                    setUpdateCoinFirebase(giftModel);
                    showGiftDialog(giftModel);
//                    showGift(giftModel);
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

    private void setUpdateCoinFirebase(GiftModel giftModel) {
        Long totalCoin = Long.valueOf(getHostCoinInfo) + Long.valueOf(giftModel.getGiftCoin());
        HashMap<String, String> data = new HashMap<>();
        data.put("currentDiamond", String.valueOf(totalCoin));
        ref.child(otherUserId).child(liveType).child(otherUserId).child("hostLiveInfo").setValue(data);
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

        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                Log.i("onTick: ", (l / 1000) + "");
            }

            @Override
            public void onFinish() {
                binding.lottieGift.setVisibility(View.GONE);
                ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").removeValue();
            }
        };
        countDownTimer.start();
    }


    private void getRequestMultiLive() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("multiLiveRequest").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.exists()) {
                    GoLiveModelClass requestModel = snapshot.getValue(GoLiveModelClass.class);
                    openRequestDialogForMultiLive(requestModel);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//                if (snapshot.exists()) {
//                    GoLiveModelClass requestModel = snapshot.getValue(GoLiveModelClass.class);
//                    openRequestDialogForMultiLive(requestModel);
//                }
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

    private List<GoLiveModelClass> liveJoinedUserList = new ArrayList<>();

    private void getMultiLiveRequest() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("multiLiveRequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(CallActivity.this, "exists", Toast.LENGTH_SHORT).show();
                    requestMultiLiveList.clear();
                    liveJoinedUserList.clear();

                    GoLiveModelClass hostUserDetails = new GoLiveModelClass();
                    hostUserDetails.setUserID(otherUserId);
                    hostUserDetails.setUserName(getChannelName);
                    hostUserDetails.setImage(image);
                    hostUserDetails.setName(name);
                    liveJoinedUserList.add(0, hostUserDetails);

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        GoLiveModelClass goLiveModelClass = snapshot1.getValue(GoLiveModelClass.class);
                        if (!goLiveModelClass.getRequestStatus().equalsIgnoreCase("2")) {
                            requestMultiLiveList.add(goLiveModelClass);
                        }
                        if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("1")) {
                            liveJoinedUserList.add(goLiveModelClass);
                        }

                    }

                    if (userJoinedAdapter != null) {
                        userJoinedAdapter.notifyDataSetChanged();
                    }

                    setRequestListAdapter(requestMultiLiveList);
                    binding.txtTotalRequest.setText("" + requestMultiLiveList.size());
                    requestMultiLiveAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CallActivity.this, "not exists", Toast.LENGTH_SHORT).show();

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
                    setMultiLiveRequestAcceptRejecte(goLiveModelClass, "1");
                }

                @Override
                public void setOnRequestRejectedListner(GoLiveModelClass goLiveModelClass) {
                    requestListMultiBottomSheet.dismiss();
                    setMultiLiveRequestAcceptRejecte(goLiveModelClass, "2");

                }

                @Override
                public void setOnRemoveUserListener(GoLiveModelClass goLiveModelClass) {
                    requestListMultiBottomSheet.dismiss();
                    setMultiLiveRequestAcceptRejecte(goLiveModelClass, "3");
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
            Glide.with(CallActivity.this).load(goLiveModelClass.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(requestMultiLiveBinding.imgProfile);
        } catch (Exception e) {

        }

        requestMultiLiveBinding.txtUserName.setText(goLiveModelClass.getName() + " wants to join with you in live session.");

        requestMultiLiveBinding.btnAccept.setOnClickListener(view -> {
            alertDialog.dismiss();
            setMultiLiveRequestAcceptRejecte(goLiveModelClass, "1");

        });

        requestMultiLiveBinding.btnRejected.setOnClickListener(view -> {
            alertDialog.dismiss();
            setMultiLiveRequestAcceptRejecte(goLiveModelClass, "2");
        });

        requestMultiLiveBinding.imgClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

    }

    private void setMultiLiveRequestAcceptRejecte(GoLiveModelClass goLiveModelClass, String status) {
        goLiveModelClass.setRequestStatus(status);
        ref.child(otherUserId).child(liveType).child(otherUserId).child("multiLiveRequest").child(goLiveModelClass.getUserID()).setValue(goLiveModelClass);
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
                Toast.makeText(CallActivity.this, "" + createLiveHistoryModel.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CallActivity.this, "You are muted by Host", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CallActivity.this, "You are unMute by Host", Toast.LENGTH_SHORT).show();
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
                binding.rlRequestMultiLive.setVisibility(View.VISIBLE);
                getMultiLiveRequestStatus();
                //multilive
            }
        } else {
// if user is host

            if (liveType.equalsIgnoreCase("singleLive")) {
                binding.rlMultiLiveRequest.setVisibility(View.GONE);
            } else {
                binding.rlMultiLiveRequest.setVisibility(View.VISIBLE);
            }

            GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
            goLiveModelClass.setUserID(CommonUtils.Companion.getUserId());
            goLiveModelClass.setCurrentDiamond(CommonUtils.Companion.getCurrentDiamond());
            goLiveModelClass.setImage(CommonUtils.Companion.getImage());
            goLiveModelClass.setLiveStatus(liveStatus);
            goLiveModelClass.setLiveType(liveType);
            goLiveModelClass.setLive(true);
            ref.child(CommonUtils.Companion.getUserId()).child(liveType).child(CommonUtils.Companion.getUserId()).child("hostLiveInfo").setValue(goLiveModelClass);
            binding.llOption.setVisibility(View.VISIBLE);
            binding.rlGiftHeart.setVisibility(View.GONE);
            sendWelcomeMessageFirebase();
            getRequestMultiLive();

        }

        if (liveStatus.equalsIgnoreCase("host")) {
            exitLiveStream();
        }

        getCommentChatMessageFirebase();
//        getViewerListFirebase();
        getHeart();
        getGift();
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
        } else {
            rtcEngine().enableLocalVideo(false);
            rtcEngine().enableLocalAudio(false);
        }

        mGridVideoViewContainer.initViewContainer(this, 0, mUidsList, mIsLandscape, mGridVideoViewContainer, RecyclerView.VERTICAL); // first is now full view
        joinChannel(getChannelName, config().mUid, getAccessToken);

        optional();


//        MultiLiveModel multiLiveModel = new MultiLiveModel();
//        multiLiveModel.setChannleName(channelName);
//        multiLiveModel.setUiid("" + (config().mUid & 0xFFFFFFFFL));
//        multiLiveModel.setCheck(status);
//        multiLiveModel.setRandomNumber(1);
//        ref.child(channelName).child("22").setValue(multiLiveModel);


//        ref.child(channelName).child("20").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (snapshot.exists()) {
//                            showLongToast("sdfghl;");
//                            MultiLiveModel multiLiveModel1 = snapshot.getValue(MultiLiveModel.class);
//                            imgGift.setVisibility(View.GONE);
//                            isShowGift = true;
//                            showGiftImage(multiLiveModel1);
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {
//
//            }
//        });


//        if (status) {
//            btnGift.setVisibility(View.VISIBLE);
//        } else {
//            btnGift.setVisibility(View.GONE);
//        }


//        btnGift.setOnClickListener(view -> {
//            MultiLiveModel multiLiveModel2 = new MultiLiveModel();
//            multiLiveModel2.setChannleName(channelName);
//            multiLiveModel2.setUiid("" + (config().mUid & 0xFFFFFFFFL));
//            multiLiveModel2.setCheck(status);
//            multiLiveModel2.setReceivedGift("https://omninos.me/donaLive/uploads/users/1603697646_birthday%20cake.gif");
//            int min = 0;
//            int max = 100;
//            int b = (int) (Math.random() * (max - min + 1) + min);
//            multiLiveModel2.setRandomNumber(b);
//            ref.child(channelName).child("20").setValue(multiLiveModel2);
//        });


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

            //            sendGiftFirebase();

        });


        binding.rlRequestMultiLive.setOnClickListener(view -> {
            sendRequestForMultiLive();
        });


        binding.rlMultiLiveRequest.setOnClickListener(view -> {
            openRequestMultiLiveDialog();
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
        sendCustomeMessage("Sends you gift", detail.getImage());
    }

    private void getMultiLiveRequestStatus() {
        ref.child(otherUserId).child(liveType).child(otherUserId).child("multiLiveRequest").child(CommonUtils.Companion.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GoLiveModelClass goLiveModelClass = snapshot.getValue(GoLiveModelClass.class);
                    if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("1")) {
                        isLiveConnected = true;
                        Toast.makeText(CallActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                        setViewerGoLiveWithHost();
                    } else if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("2")) {
                        isLiveConnected = false;
                        Toast.makeText(CallActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    } else if (goLiveModelClass.getRequestStatus().equalsIgnoreCase("3")) {
                        isLiveConnected = false;
                        removeByHostLive();
                        Toast.makeText(CallActivity.this, "Remove By Host", Toast.LENGTH_SHORT).show();
                    } else {
                        isLiveConnected = false;
                        Toast.makeText(CallActivity.this, "Waiting for host to accept request", Toast.LENGTH_SHORT).show();
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

    private void setViewerGoLiveWithHost() {
        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
        preview(true, surfaceV, 0);
        surfaceV.setZOrderOnTop(false);
        surfaceV.setZOrderMediaOverlay(false);
        mUidsList.put(0, surfaceV); // get first surface view
        rtcEngine().enableLocalVideo(true);
        rtcEngine().enableLocalAudio(true);
        mGridVideoViewContainer.initViewContainer(this, 0, mUidsList, mIsLandscape, mGridVideoViewContainer, RecyclerView.VERTICAL); // first is now full view
        binding.llOption.setVisibility(View.VISIBLE);
        binding.rlMultiLiveRequest.setVisibility(View.GONE);
        binding.rlRequestMultiLive.setVisibility(View.GONE);
    }


    private void openRequestMultiLiveDialog() {
        requestListMultiBottomSheet = new BottomSheetDialog(this);
        DialogRequestMultiliveListBinding dialogGiftBinding = DialogRequestMultiliveListBinding.inflate(LayoutInflater.from(this));
        requestListMultiBottomSheet.setContentView(dialogGiftBinding.getRoot());
        dialogGiftBinding.recyclerRequestMultiLive.setAdapter(requestMultiLiveAdapter);
        requestListMultiBottomSheet.show();

    }

    private void sendRequestForMultiLive() {
        GoLiveModelClass goLiveModelClass = new GoLiveModelClass();
        goLiveModelClass.setUserID(CommonUtils.Companion.getUserId());
        String image = NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getImage();
        goLiveModelClass.setImage(image);
        goLiveModelClass.setName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getName());
        goLiveModelClass.setUserName(NoteApplication.Companion.getAppPreference1().getUserDetails(AppConstants.Companion.getUSER_DETAIL(), Details.class).getUsername());
        goLiveModelClass.setRequestStatus("0");
        ref.child(otherUserId).child(liveType).child(otherUserId).child("multiLiveRequest").child(CommonUtils.Companion.getUserId()).setValue(goLiveModelClass);
    }

    private List<GiftsListModel.Detail> giftList = new ArrayList<>();

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
                    dialogGiftBinding.txtTotalDiamond.setText(CommonUtils.Companion.prettyCount(Long.valueOf(giftsListModel.getDimaond())));
                }
                Toast.makeText(CallActivity.this, "" + giftsListModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        giftBottomDialogSheet.show();

    }

    private void setGiftListAdapter(List<GiftsListModel.Detail> details, DialogGiftBinding dialogGiftBinding, BottomSheetDialog giftBottomDialogSheet) {

        GiftAdapter giftAdapter = new GiftAdapter(this, details, new GiftAdapter.Click() {
            @Override
            public void setOnSendGiftListener(GiftsListModel.Detail detail) {
                giftBottomDialogSheet.dismiss();
                if (liveType.equalsIgnoreCase("singleLive")) {
                    hitSendGiftApi(CommonUtils.Companion.getUserId(), otherUserId, detail.getAmount(), detail.getId(), liveId, detail);
                } else {
                    if (liveJoinedUserList.size() <= 1) {
                        hitSendGiftApi(CommonUtils.Companion.getUserId(), otherUserId, detail.getAmount(), detail.getId(), liveId, detail);
                    } else {
                        openDialogUsersLiveJoined(detail);
                    }
                }

            }
        });
        dialogGiftBinding.recyclerGift.setAdapter(giftAdapter);
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
            }
        });
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
                sendGiftFirebase(detail);
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

    private void onBigVideoViewClicked(View view, int position) {
        log.debug("onItemClick " + view + " " + position + " " + mLayoutType);
        toggleFullscreen();
    }

    private void onBigVideoViewDoubleClicked(View view, int position) {
        log.debug("onItemDoubleClick " + view + " " + position + " " + mLayoutType);

        if (mUidsList.size() < 2) {
            return;
        }

        UserStatusData user = mGridVideoViewContainer.getItem(position);
        int uid = (user.mUid == 0) ? config().mUid : user.mUid;

        if (mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 1) {
            switchToSmallVideoView(uid);
        } else {
            switchToDefaultVideoView();
        }
    }

    private void onSmallVideoViewDoubleClicked(View view, int position) {
        log.debug("onItemDoubleClick small " + view + " " + position + " " + mLayoutType);

        switchToDefaultVideoView();
    }

    private void showOrHideStatusBar(boolean hide) {
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = decorView.getSystemUiVisibility();

            if (hide) {
                uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            } else {
                uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void toggleFullscreen() {
        mFullScreen = !mFullScreen;

        showOrHideCtrlViews(mFullScreen);

        mUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showOrHideStatusBar(mFullScreen);
            }
        }, 200); // action bar fade duration
    }

    private void showOrHideCtrlViews(boolean hide) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (hide) {
                ab.hide();
            } else {
                ab.show();
            }
        }
    }

    private void optional() {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private void optionalDestroy() {
    }

    private int getVideoEncResolutionIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncResolutionIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX);
        if (videoEncResolutionIndex > VIDEO_DIMENSIONS.length - 1) {
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
        VideoEncoderConfiguration.VideoDimensions videoDimension = VIDEO_DIMENSIONS[getVideoEncResolutionIndex()];
        VideoEncoderConfiguration.FRAME_RATE videoFps = ConstantApp.VIDEO_FPS[getVideoEncFpsIndex()];
        configEngine(videoDimension, videoFps, encryptionKey, encryptionMode);
    }

    public void onSwitchCameraClicked(View view) {
        RtcEngine rtcEngine = rtcEngine();
        // Switches between front and rear cameras.
        rtcEngine.switchCamera();
    }

    public void onSwitchSpeakerClicked(View view) {
        RtcEngine rtcEngine = rtcEngine();
        /*
          Enables/Disables the audio playback route to the speakerphone.
          This method sets whether the audio is routed to the speakerphone or earpiece.
          After calling this method, the SDK returns the onAudioRouteChanged callback
          to indicate the changes.
         */
        rtcEngine.setEnableSpeakerphone(mAudioRouting != Constants.AUDIO_ROUTE_SPEAKERPHONE);
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
    }

    @Override
    protected void deInitUIandEvent() {
        if (!liveStatus.equalsIgnoreCase("host")) {
            setLiveStreamOffline();
            hitEndLiveApi(createLiveHistory.getDetails().getId(), getCurrentTime());
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
        setMultiLiveRequestAcceptRejecte(goLiveModelClass, "2");
    }

    private void doLeaveChannel() {
        leaveChannel(config().mChannel);
        preview(false, null, 0);
    }

    public void onHangupClicked(View view) {
        log.info("onHangupClicked " + view);

        finish();
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
            rtcEngine.disableVideo();
        } else {
            rtcEngine.enableVideo();
        }

        ImageView iv = (ImageView) view;

        iv.setImageResource(mVideoMuted ? R.drawable.ic_baseline_videocam_off_24 : R.drawable.ic_baseline_videocam_24);

        hideLocalView(mVideoMuted);
    }

    private SurfaceView getLocalView() {
        for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == config().mUid) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void hideLocalView(boolean hide) {
        int uid = config().mUid;
        doHideTargetView(uid, hide);
    }

    private void doHideTargetView(int targetUid, boolean hide) {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(targetUid, hide ? UserStatusData.VIDEO_MUTED : UserStatusData.DEFAULT_STATUS);
        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
            mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
        } else if (mLayoutType == LAYOUT_TYPE_SMALL) {
            UserStatusData bigBgUser = mGridVideoViewContainer.getItem(0);
            if (bigBgUser.mUid == targetUid) { // big background is target view
                mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
            } else { // find target view in small video view list
                log.warn("SmallVideoViewAdapter call notifyUiChanged " + mUidsList + " " + (bigBgUser.mUid & 0xFFFFFFFFL) + " target: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status);
                mSmallVideoViewAdapter.notifyUiChanged(mUidsList, bigBgUser.mUid, status, null);
            }
        }
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

//ToDo


//        doRenderRemoteUi(uid);

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


            }
        });
    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        log.debug("onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        doRemoveRemoteUi(uid);
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

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

        if (state == 0) {

        } else {
            doRenderRemoteUi(uid);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("onRemoteVide", "" + uid + "  " + state + "  ");

            }
        });

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

                doHideTargetView(peerUid, muted);

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
        mGridVideoViewContainer.initViewContainer(this, config().mUid, mUidsList, mIsLandscape, mGridVideoViewContainer, RecyclerView.VERTICAL);

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

        mGridVideoViewContainer.initViewContainer(this, bigBgUid, slice, mIsLandscape, mGridVideoViewContainer, RecyclerView.VERTICAL);

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
