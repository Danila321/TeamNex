package com.myappteam.projectapp.ui.feedback;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myappteam.projectapp.R;
import com.myappteam.projectapp.databinding.FragmentFeedbackBinding;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

public class FeedbackFragment extends Fragment {
    private BannerAdView mBannerAd = null;
    private FragmentFeedbackBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Кнопка копирования кода в буфер обмена
        ImageButton copyEmail = root.findViewById(R.id.FeedbackButtonCopyCode);
        copyEmail.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("email", "teamdraftsup@gmail.com");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), R.string.feedback_copy_text, Toast.LENGTH_SHORT).show();
        });

        binding.adContainerViewFeedback.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        binding.adContainerViewFeedback.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mBannerAd = loadBannerAd(getAdSize());
                    }
                }
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @NonNull
    private BannerAdSize getAdSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Calculate the width of the ad, taking into account the padding in the ad container.
        int adWidthPixels = binding.adContainerViewFeedback.getWidth();
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels;
        }
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);

        return BannerAdSize.stickySize(getContext(), adWidth);
    }

    @NonNull
    private BannerAdView loadBannerAd(@NonNull final BannerAdSize adSize) {
        final BannerAdView bannerAd = binding.adContainerViewFeedback;
        bannerAd.setAdSize(adSize);
        bannerAd.setAdUnitId("R-M-11467421-3");
        bannerAd.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (getActivity() != null) {
                    if (getActivity().isDestroyed() && mBannerAd != null) {
                        mBannerAd.destroy();
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
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
                // Called when an impression is recorded for an ad.
            }
        });
        final AdRequest adRequest = new AdRequest.Builder()
                // Methods in the AdRequest.Builder class can be used here to specify individual options settings.
                .build();
        bannerAd.loadAd(adRequest);
        return bannerAd;
    }
}