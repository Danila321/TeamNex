package com.myappteam.projectapp.ui.home.workSpace.usersActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.ui.home.workSpace.cardActivity.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.nativeads.NativeAd;
import com.yandex.mobile.ads.nativeads.NativeAdEventListener;
import com.yandex.mobile.ads.nativeads.NativeAdException;
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener;
import com.yandex.mobile.ads.nativeads.NativeAdLoader;
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration;
import com.yandex.mobile.ads.nativeads.NativeAdView;
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder;
import com.yandex.mobile.ads.nativeads.template.NativeBannerView;

import java.util.ArrayList;
import java.util.Objects;

public class UsersAdapter extends ArrayAdapter<UserRole> {
    private static final int TYPE_USER = 0;
    private static final int TYPE_AD = 1;
    @Nullable
    private NativeAdLoader mNativeAdLoader = null;
    private NativeAd nativeAd = null;
    Context context;
    String boardId;
    FragmentManager fragmentManager;
    boolean isOwner;
    ArrayList<UserRole> users;

    public UsersAdapter(@NonNull Context context, ArrayList<UserRole> users, String boardId, FragmentManager fragmentManager, boolean isOwner) {
        super(context, R.layout.workspace_activity_users_item, users);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.users = users;
        this.boardId = boardId;
        this.isOwner = isOwner;

        mNativeAdLoader = createNativeAdLoader();
        if (mNativeAdLoader != null) {
            // Methods in the NativeAdRequestConfiguration.Builder class can be used here to specify individual options settings.
            mNativeAdLoader.loadAd(
                    new NativeAdRequestConfiguration.Builder("R-M-17485023-4").build()
            );
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int type = getItemViewType(position);

        if (type == TYPE_USER) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_activity_users_item, null);
            }

            UserRole userRole = getItem(position);
            User user = userRole.getUser();
            String role = userRole.getRole();

            ImageView image = convertView.findViewById(R.id.UsersActivityImage);
            Glide.with(context).load(user.getPhoto()).into(image);

            TextView name = convertView.findViewById(R.id.UsersActivityName);
            name.setText(user.getName());

            ConstraintLayout roleLayout = convertView.findViewById(R.id.UsersActivityRoleLayout);
            roleLayout.setOnClickListener(v -> {
                UsersSetRoleDialog dialog = UsersSetRoleDialog.newInstance(role, boardId, user.getId());
                dialog.show(fragmentManager, "setUserRole");
            });

            ImageButton delete = convertView.findViewById(R.id.UsersActivityDelete);
            delete.setOnClickListener(v -> {
                DeleteUserDialog dialog = DeleteUserDialog.newInstance(boardId, user.getId());
                dialog.show(fragmentManager, "deleteUser");
            });

            ImageView roleImage = convertView.findViewById(R.id.UsersActivityRoleImage);
            TextView roleText = convertView.findViewById(R.id.UsersActivityRoleText);

            if (Objects.equals(FirebaseAuth.getInstance().getUid(), user.getId())) {
                name.setText(name.getText() + " (Вы)");
            }

            if (isOwner) {
                if (role.equals("owner")) {
                    roleText.setText("Владелец");
                    roleText.setTextColor(ContextCompat.getColor(context, R.color.black));
                    roleLayout.setElevation(0);
                    roleLayout.setClickable(false);
                    roleImage.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                } else if (role.equals("admin")) {
                    roleText.setText("Админ");
                } else {
                    roleText.setText("Участник");
                }
            } else {
                if (role.equals("owner")) {
                    roleText.setText("Владелец");
                } else if (role.equals("admin")) {
                    roleText.setText("Админ");
                } else {
                    roleText.setText("Участник");
                }
                roleText.setTextColor(ContextCompat.getColor(context, R.color.black));
                roleLayout.setElevation(0);
                roleLayout.setClickable(false);
                roleImage.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        } else if (type == TYPE_AD) {
            //Просто передаем пустую view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_activity_users_ad, null);
            convertView.setVisibility(View.GONE);
            //Если реклама сформировалась, то подгружаем необходимый экран и показываем рекламу
            if (nativeAd != null) {
                NativeAdView nativeAdView;
                if (nativeAd.getAdAssets().getIcon() == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_activity_users_ad, null);
                    nativeAdView = convertView.findViewById(R.id.native_ad_container);
                } else {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_activity_users_ad_icon, null);
                    nativeAdView = convertView.findViewById(R.id.native_ad_container_icon);
                }
                nativeAdView.setVisibility(View.GONE);
                showAd(convertView, nativeAdView);
            }
        }

        return convertView;
    }

    private void showAd(View convertView, NativeAdView nativeAdView) {
        final NativeAdViewBinder nativeAdViewBinder = new NativeAdViewBinder.Builder(nativeAdView)
                .setAgeView(convertView.findViewById(R.id.age))
                .setBodyView(convertView.findViewById(R.id.body))
                .setCallToActionView(convertView.findViewById(R.id.call_to_action))
                .setDomainView(convertView.findViewById(R.id.domain))
                .setFaviconView(convertView.findViewById(R.id.favicon))
                .setFeedbackView(convertView.findViewById(R.id.feedback))
                .setIconView(convertView.findViewById(R.id.icon))
                .setMediaView(convertView.findViewById(R.id.media))
                .setPriceView(convertView.findViewById(R.id.price))
                .setRatingView(convertView.findViewById(R.id.rating))
                .setReviewCountView(convertView.findViewById(R.id.review_count))
                .setSponsoredView(convertView.findViewById(R.id.sponsored))
                .setTitleView(convertView.findViewById(R.id.title))
                .setWarningView(convertView.findViewById(R.id.warning))
                .build();
        try {
            nativeAd.bindNativeAd(nativeAdViewBinder);
            nativeAd.setNativeAdEventListener(new NativeAdEventLogger());
            nativeAdView.setVisibility(View.VISIBLE);
        } catch (final NativeAdException exception) {
            Log.e("TAG", exception.getMessage());
        }
    }

    private static class NativeAdEventLogger implements NativeAdEventListener {
        @Override
        public void onAdClicked() {

        }

        @Override
        public void onLeftApplication() {
            // Called when user is about to leave application (e.g., to go to the browser), as a result of clicking on the ad.
        }

        @Override
        public void onReturnedToApplication() {
            // Called when user returned to application after click.
        }

        @Override
        public void onImpression(@Nullable ImpressionData impressionData) {

        }
    }

    private NativeAdLoader createNativeAdLoader() {
        if (mNativeAdLoader != null) {
            return mNativeAdLoader;
        }

        final NativeAdLoader newNativeAdLoader = new NativeAdLoader(context);
        newNativeAdLoader.setNativeAdLoadListener(new NativeAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final NativeAd na) {
                // The ad was loaded successfully. Now you can show loaded ad.
                Log.i("YandexAds", "Native ad loaded");
                nativeAd = na;
                notifyDataSetChanged();
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError error) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                Log.i("YandexAds", "Native ad failed to load: " + error);
            }
        });
        return newNativeAdLoader;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < users.size()) {
            return TYPE_USER;
        }
        return TYPE_AD;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return users.size() + 1;
    }
}