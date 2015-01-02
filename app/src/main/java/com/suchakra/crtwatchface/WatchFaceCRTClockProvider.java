package com.suchakra.crtwatchface;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

public class WatchFaceCRTClockProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		updateWidgets(context);
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String action = intent.getAction();
		if (Intent.ACTION_TIME_TICK.equals(action)
				|| Intent.ACTION_TIME_CHANGED.equals(action)) {
			updateWidgets(context);
		}        
        else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            updateBattWidget(context, intent);
        }
    }

    private void updateBattWidget(Context context, Intent intent){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.crt_widget);

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        
        if (level == 100) {
            views.setImageViewBitmap(R.id.line6, getFontBitmap(context, "|" + String.valueOf(level), Color.rgb(229, 148, 0), 22));
        }
        else {
            views.setImageViewBitmap(R.id.line6, getFontBitmap(context, "|" + String.valueOf(level) + "%", Color.rgb(229, 148, 0), 22));

        }
        System.out.println(String.valueOf(level));

        manager.updateAppWidget(new ComponentName(context,
                WatchFaceCRTClockProvider.class), views);
    }
    
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		Intent serviceIntent = new Intent(context, CRTClockService.class);
		context.startService(serviceIntent);
		
		registerReceivers(context);
	}

	private void registerReceivers(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.getApplicationContext().registerReceiver(this, filter);
	}

    public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP) {
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        
        // Prepare ghostly glow
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "arcade.ttf");
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        int glowRadius = 5;
        // glow
        paint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));
        paint.setColor(color);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth + 5, height + 5, Bitmap.Config.ARGB_4444);

        
        // Prepare original text
        Paint painttxt = new Paint();
        Typeface typefacetxt = Typeface.createFromAsset(context.getAssets(), "arcade.ttf");
        painttxt.setAntiAlias(true);
        painttxt.setTypeface(typefacetxt);
        painttxt.setColor(color);
        painttxt.setTextSize(fontSizePX);
        
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);
        canvas.drawText(text, xOriginal, fontSizePX, painttxt);
        return bitmap;
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }
    
	@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	private void updateWidgets(Context context) {
		String timeHour = "HH";
        String timeMin = "mm";
        String day = "|E";
        String month = "|MMM";
        String dat = "|d";
		Date date = new Date();

		String textHour = new SimpleDateFormat(timeHour).format(date);
        String textMin = new SimpleDateFormat(timeMin).format(date);
        String textDay = new SimpleDateFormat(day).format(date);
        String textMonth = new SimpleDateFormat(month).format(date);
        String textDate = new SimpleDateFormat(dat).format(date);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.crt_widget);
        
        views.setImageViewBitmap(R.id.time_hours, getFontBitmap(context, textHour, Color.rgb(229,148,0), 47));
        views.setImageViewBitmap(R.id.time_min, getFontBitmap(context, textMin, Color.rgb(229,148,0), 47));
        views.setImageViewBitmap(R.id.day, getFontBitmap(context, textDay, Color.rgb(229,148,0), 22));
        views.setImageViewBitmap(R.id.month, getFontBitmap(context, textMonth, Color.rgb(229,148,0), 22));
        views.setImageViewBitmap(R.id.date, getFontBitmap(context, textDate, Color.rgb(229,148,0), 22));

        views.setImageViewBitmap(R.id.line4, getFontBitmap(context, "|---", Color.rgb(229,148,0), 22));
        views.setImageViewBitmap(R.id.line5, getFontBitmap(context, "|BAT", Color.rgb(229,148,0), 22));
//        views.setImageViewBitmap(R.id.line6, getFontBitmap(context, "|", Color.rgb(229,148,0), 22));
//        views.setImageViewBitmap(R.id.line7, getFontBitmap(context, "|", Color.rgb(229,148,0), 10));


//        views.setTextViewText(R.id.date, textDate.toUpperCase());
/*		views.setImageViewResource(R.id.hours_first_value,
				numbers[Character.getNumericValue(textTime.charAt(0))]);
		
		views.setImageViewResource(R.id.hours_second_value,
				numbers[Character.getNumericValue(textTime.charAt(1))]);
		
		views.setImageViewResource(R.id.minutes_first_value,
				numbers[Character.getNumericValue(textTime.charAt(3))]);
		
		views.setImageViewResource(R.id.minutes_second_value,
				numbers[Character.getNumericValue(textTime.charAt(4))]);*/

		manager.updateAppWidget(new ComponentName(context,
				WatchFaceCRTClockProvider.class), views);
	}
}
