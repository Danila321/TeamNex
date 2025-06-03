package com.teamnexapp.teamnex.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.databinding.FragmentSettingsBinding;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends Fragment implements OnChangeSettings {
    private BannerAdView mBannerAd = null;
    private FragmentSettingsBinding binding;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String currentLanguage = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("language", "ru");
        TextView languageTextView = root.findViewById(R.id.settingsLanguageText);
        if (currentLanguage.equals("ru")) {
            languageTextView.setText("Русский");
        } else {
            languageTextView.setText("English");
        }

        ConstraintLayout language = root.findViewById(R.id.settingsLanguage);
        language.setOnClickListener(v -> {
            BottomSheetDialogLanguage dialog = BottomSheetDialogLanguage.newInstance(currentLanguage);
            dialog.show(getChildFragmentManager(), "chooseLanguageDialog");
        });

        binding.adContainerViewSettings.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        binding.adContainerViewSettings.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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

    @Override
    public void onChangeLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = getActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putString("language", language).apply();
        getActivity().recreate();
    }

    @NonNull
    private BannerAdSize getAdSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Calculate the width of the ad, taking into account the padding in the ad container.
        int adWidthPixels = binding.adContainerViewSettings.getWidth();
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels;
        }
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);

        return BannerAdSize.stickySize(getContext(), adWidth);
    }

    @NonNull
    private BannerAdView loadBannerAd(@NonNull final BannerAdSize adSize) {
        final BannerAdView bannerAd = binding.adContainerViewSettings;
        bannerAd.setAdSize(adSize);
        bannerAd.setAdUnitId("R-M-11467421-1");
        bannerAd.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (getActivity() != null){
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