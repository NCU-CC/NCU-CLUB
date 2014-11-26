package evan.ncu.club;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
//import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Evany on 2014/11/2.
 */
public class MyExpandableListItemAdapter extends
        ExpandableListItemAdapter<ListData> {
    private final Context mContext;
    private final BitmapCache mMemoryCache;
    private int mActionViewResId;
    /**
     * Creates a new ExpandableListItemAdapter with the specified list, or
     * an empty list if items == null.
     */
    public MyExpandableListItemAdapter(final Context context,
                                        final ArrayList<ListData> items) {

        super(context, R.layout.expandablelistitem_card,
                R.id.expandablelistitem_card_title,
                R.id.expandablelistitem_card_content, items);
        mContext = context;
        mMemoryCache = new BitmapCache();

    }

    @Override
    public View getTitleView(final int position, final View convertView,
                             final ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.expandablelistitem_title, null);
        TextView tv = (TextView) v.findViewById(R.id.item_Title);
        if (tv == null) {
            tv = new TextView(mContext);
        }
        tv.setText(getItem(position).title);
        tv.setTextColor(Color.BLACK);
        tv.setAlpha(0.87f);
        tv.setTextSize(16);

        tv = (TextView) v.findViewById(R.id.item_Date);
        tv.setText(getItem(position).time);
        tv.setTextColor(Color.BLACK);
        tv.setAlpha(0.54f);
        tv.setTextSize(14);
        Button addButton = (Button) v.findViewById(R.id.Addbutton);
        addButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                try {
                    addCalendarEvent(getItem(position).title, getItem(position).content, getItem(position).time);
                } catch (ParseException e) {
                    Log.e("Debug",e.toString());
                    e.printStackTrace();
                }

            }

        });

        return v;
    }

    @Override
    public View getContentView(final int position, final View convertView,
                               final ViewGroup parent) {

        View v = LayoutInflater.from(mContext).inflate(
                R.layout.expandablelistitem_content, parent, false);

        TextView textView = (TextView) v.findViewById(R.id.contentTextView);
        textView.setText(getItem(position).content);
        textView.setTextColor(Color.BLACK);
        textView.setAlpha(0.53f);

        return v;
    }

    private void addBitmapToMemoryCache(final int key, final Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(final int key) {
        return mMemoryCache.get(key);
    }

    public void addCalendarEvent(String title, String description, String time) throws ParseException {

        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = df.parse(time);
        cal.setTime(date);
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        mContext.startActivity(intent);
    }


}
