package com.poojan.edge.volume.control;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

/**
 * Created by Poojan on 5/27/2017.
 */

public class EdgeSingleProvider extends SlookCocktailProvider {

    private static final String ACTION_REMOTE_CLICK = "com.example.cocktailslooksample.action.ACTION_REMOTE_CLICK";

    private RemoteViews mRemoteViews;
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        getNotificationManager(context);

        switch (action) {
            case ACTION_REMOTE_CLICK:
                performRemoteClick(context, intent);
                break;

            case AudioManager.RINGER_MODE_CHANGED_ACTION:

                if (mRemoteViews == null) {
                    mRemoteViews = createRemoteView(context);
                }

                SlookCocktailManager cocktailManager = SlookCocktailManager.getInstance(context);
                int[] cocktailIds = cocktailManager.getCocktailIds(new ComponentName(context, EdgeSingleProvider.class));

                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                updateUIOnModeChange(am, mRemoteViews);
                if (AudioManager.RINGER_MODE_NORMAL == am.getRingerMode()) {
                    updateUIRingerChange(am, mRemoteViews);
                    updateUINotificationChange(am, mRemoteViews);
                    updateUISystemChange(am, mRemoteViews);
                }
                cocktailManager.updateCocktail(cocktailIds[0], mRemoteViews);
                break;
        }

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        getNotificationManager(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !mNotificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
            Toast.makeText(context, "Please allow Do Not Disturb permission", Toast.LENGTH_LONG).show();
        }
    }

    private void performRemoteClick(Context context, Intent intent) {
        if (mRemoteViews == null) {
            mRemoteViews = createRemoteView(context);
        }

        SlookCocktailManager cocktailManager = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = cocktailManager.getCocktailIds(new ComponentName(context, EdgeSingleProvider.class));
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int id = intent.getIntExtra("id", -1);
        switch (id) {
            case R.id.ringtone_plus:
                changeVolume(am, AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE/*, mRemoteViews, R.id.ringtone_level*/);
                updateUIRingerChange(am, mRemoteViews);
                break;

            case R.id.ringtone_minus:
                changeVolume(am, AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER/*, mRemoteViews, R.id.ringtone_level*/);
                updateUIRingerChange(am, mRemoteViews);
                break;

            case R.id.media_plus:
                changeVolume(am, AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE/*, mRemoteViews, R.id.media_level*/);
                updateUIMediaChange(am, mRemoteViews);
                break;

            case R.id.media_minus:
                changeVolume(am, AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER/*, mRemoteViews, R.id.media_level*/);
                updateUIMediaChange(am, mRemoteViews);
                break;

            case R.id.notification_plus:
                if (am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE
                        && am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    changeVolume(am, AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_RAISE/*, mRemoteViews, R.id.notification_level*/);
                    updateUINotificationChange(am, mRemoteViews);
                }
                break;

            case R.id.notification_minus:
                if (am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE
                        && am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    changeVolume(am, AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_LOWER/*, mRemoteViews, R.id.notification_level*/);
                    updateUINotificationChange(am, mRemoteViews);
                }
                break;

            case R.id.system_plus:
                if (am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE
                        && am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    changeVolume(am, AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_RAISE/*, mRemoteViews, R.id.system_level*/);
                    updateUISystemChange(am, mRemoteViews);
                }
                break;

            case R.id.system_minus:
                if (am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE
                        && am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    changeVolume(am, AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_LOWER/*, mRemoteViews, R.id.system_level*/);
                    updateUISystemChange(am, mRemoteViews);
                }
                break;

            case R.id.ring_mode_button:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && mNotificationManager.isNotificationPolicyAccessGranted()) {
                    setRingerMode(am, mRemoteViews, R.id.ringer_mode_tv);
                } else {
                    Toast.makeText(context, "Permission for notification access is required", Toast.LENGTH_SHORT).show();
                }
                updateUIRingerChange(am, mRemoteViews);
                updateUINotificationChange(am, mRemoteViews);
                updateUISystemChange(am, mRemoteViews);
                break;

            default:
                break;
        }
        cocktailManager.updateCocktail(cocktailIds[0], mRemoteViews);
    }

    private void setRingerMode(AudioManager am, RemoteViews remoteViews, int ringer_mode_tv) {

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                remoteViews.setTextViewText(ringer_mode_tv, "Vibrate");
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                remoteViews.setTextViewText(ringer_mode_tv, "Silent");
                break;

            case AudioManager.RINGER_MODE_SILENT:
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                remoteViews.setTextViewText(ringer_mode_tv, "Normal");
                break;
        }

    }

    private void changeVolume(AudioManager audioManager, int type, int volumelevel/*, RemoteViews remoteViews, int view*/) {
        audioManager.adjustStreamVolume(type, volumelevel, 0);
    }

    private void updateUIRingerChange(AudioManager audioManager, RemoteViews remoteViews) {
        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) > 0 && audioManager.getStreamVolume(AudioManager.STREAM_RING) < 15) {
            setDefaultVolume(audioManager, AudioManager.STREAM_NOTIFICATION, mRemoteViews, R.id.notification_level);
            setDefaultVolume(audioManager, AudioManager.STREAM_SYSTEM, mRemoteViews, R.id.system_level);
            setDefaultVolume(audioManager, AudioManager.STREAM_RING, mRemoteViews, R.id.ringtone_level);
            setDefaultRingMode(audioManager, mRemoteViews);
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_RING) >= 15) {
            remoteViews.setTextViewText(R.id.ringtone_level, String.valueOf("Max"));
            setDefaultRingMode(audioManager, remoteViews);
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_RING) <= 0) {
            remoteViews.setTextViewText(R.id.ringtone_level, String.valueOf("Vibrate"));
            remoteViews.setTextViewText(R.id.notification_level, String.valueOf("Vibrate"));
            remoteViews.setTextViewText(R.id.system_level, String.valueOf("Vibrate"));
            remoteViews.setTextViewText(R.id.ringer_mode_tv, String.valueOf("Vibrate"));
            setDefaultRingMode(audioManager, remoteViews);
        }
    }

    private void updateUIMediaChange(AudioManager audioManager, RemoteViews remoteViews) {

        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0 && audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < 15) {
            setDefaultVolume(audioManager, AudioManager.STREAM_MUSIC, mRemoteViews, R.id.media_level);
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) >= 15) {
            remoteViews.setTextViewText(R.id.media_level, String.valueOf("Max"));
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) <= 0) {
            remoteViews.setTextViewText(R.id.media_level, String.valueOf("Min"));
        }
    }

    private void updateUINotificationChange(AudioManager audioManager, RemoteViews remoteViews) {

        if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) > 0 && audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) < 15) {
            setDefaultVolume(audioManager, AudioManager.STREAM_NOTIFICATION, mRemoteViews, R.id.notification_level);
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) <= 0) {
            remoteViews.setTextViewText(R.id.notification_level, String.valueOf("Min"));
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) >= 15) {
            remoteViews.setTextViewText(R.id.notification_level, String.valueOf("Max"));
        }
    }

    private void updateUISystemChange(AudioManager audioManager, RemoteViews remoteViews) {

        if (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) > 0 && audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) < 15) {
            setDefaultVolume(audioManager, AudioManager.STREAM_SYSTEM, mRemoteViews, R.id.system_level);
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) <= 0) {
            remoteViews.setTextViewText(R.id.system_level, String.valueOf("Min"));
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) >= 15) {
            remoteViews.setTextViewText(R.id.system_level, String.valueOf("Max"));
        }
    }

    private void updateUIOnModeChange(AudioManager audioManager, RemoteViews remoteViews) {

        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
                || audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                remoteViews.setTextViewText(R.id.ringtone_level, String.valueOf("Silent"));
                remoteViews.setTextViewText(R.id.ringer_mode_tv, String.valueOf("Silent"));
            } else {
                remoteViews.setTextViewText(R.id.ringtone_level, String.valueOf("Vibrate"));
                remoteViews.setTextViewText(R.id.ringer_mode_tv, String.valueOf("Vibrate"));
            }
            remoteViews.setTextViewText(R.id.notification_level, String.valueOf("Min"));
            remoteViews.setTextViewText(R.id.system_level, String.valueOf("Min"));
        }
    }

    private void setDefaultVolume(AudioManager audioManager, int type, RemoteViews remoteViews, int view) {
        if (audioManager.getStreamVolume(type) > 0 && audioManager.getStreamVolume(type) < 15) {
            remoteViews.setTextViewText(view, String.valueOf(audioManager.getStreamVolume(type)));
        } else if (audioManager.getStreamVolume(type) <= 0) {
            remoteViews.setTextViewText(view, "Min");
        } else if (audioManager.getStreamVolume(type) >= 15) {
            remoteViews.setTextViewText(view, "Max");
        }
    }

    private void setDefaultRingMode(AudioManager audioManager, RemoteViews remoteViews) {
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                remoteViews.setTextViewText(R.id.ringer_mode_tv, "Normal");
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                remoteViews.setTextViewText(R.id.ringer_mode_tv, "Vibrate");
                break;

            case AudioManager.RINGER_MODE_SILENT:
                remoteViews.setTextViewText(R.id.ringer_mode_tv, "Silent");
                break;
        }
    }


    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailManager, int[] cocktailIds) {
        super.onUpdate(context, cocktailManager, cocktailIds);

        if (mRemoteViews == null) {
            mRemoteViews = createRemoteView(context);
        }

        getNotificationManager(context);

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        updateUIRingerChange(am, mRemoteViews);
        updateUINotificationChange(am, mRemoteViews);
        updateUISystemChange(am, mRemoteViews);
        updateUIMediaChange(am, mRemoteViews);
        setDefaultRingMode(am, mRemoteViews);
        cocktailManager.updateCocktail(cocktailIds[0], mRemoteViews);
    }

    private RemoteViews createRemoteView(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.button_layout);

        remoteViews.setOnClickPendingIntent(R.id.ringtone_plus, getClickIntent(context, R.id.ringtone_plus));
        remoteViews.setOnClickPendingIntent(R.id.ringtone_minus, getClickIntent(context, R.id.ringtone_minus));

        remoteViews.setOnClickPendingIntent(R.id.media_plus, getClickIntent(context, R.id.media_plus));
        remoteViews.setOnClickPendingIntent(R.id.media_minus, getClickIntent(context, R.id.media_minus));

        remoteViews.setOnClickPendingIntent(R.id.notification_plus, getClickIntent(context, R.id.notification_plus));
        remoteViews.setOnClickPendingIntent(R.id.notification_minus, getClickIntent(context, R.id.notification_minus));

        remoteViews.setOnClickPendingIntent(R.id.system_plus, getClickIntent(context, R.id.system_plus));
        remoteViews.setOnClickPendingIntent(R.id.system_minus, getClickIntent(context, R.id.system_minus));

        remoteViews.setOnClickPendingIntent(R.id.ring_mode_button, getClickIntent(context, R.id.ring_mode_button));

        return remoteViews;
    }

    private PendingIntent getClickIntent(Context context, int id) {
        Intent clickIntent = new Intent(context, EdgeSingleProvider.class);
        clickIntent.setAction(ACTION_REMOTE_CLICK);
        clickIntent.putExtra("id", id);
        clickIntent.putExtra("key", 0);
        return PendingIntent.getBroadcast(context, id, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getNotificationManager(Context context) {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}