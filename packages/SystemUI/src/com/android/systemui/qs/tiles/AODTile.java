/*
 * Copyright (C) 2018 The OmniROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QsEventLogger;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.res.R;
import com.android.systemui.statusbar.policy.KeyguardStateController;

import javax.inject.Inject;

public class AODTile extends QSTileImpl<BooleanState> {
    private boolean mAodDisabled;
    private final Icon mIcon = ResourceIcon.get(R.drawable.ic_qs_aod);

    public static final String TILE_SPEC = "aod";

    @Inject
    public AODTile(QSHost host,
            QsEventLogger uiEventLogger,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger,
            BroadcastDispatcher broadcastDispatcher,
            KeyguardStateController keyguardStateController
        ) {
        super(host, uiEventLogger, backgroundLooper, mainHandler, falsingManager, metricsLogger,
                statusBarStateController, activityStarter, qsLogger);
        mAodDisabled = Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON, 1) == 0;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    public void handleClick(@Nullable View view) {
        mAodDisabled = !mAodDisabled;
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON,
                mAodDisabled ? 0 : 1);
        refreshState();
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_aod_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.icon = mIcon;
        state.value = mAodDisabled;
        state.label = mContext.getString(R.string.quick_settings_aod_label);
        if (mAodDisabled) {
            state.state = Tile.STATE_INACTIVE;
        } else {
            state.state = Tile.STATE_ACTIVE;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CHERISH_SETTINGS;
    }

    @Override
    public void handleSetListening(boolean listening) {
        // Do nothing
    }
}