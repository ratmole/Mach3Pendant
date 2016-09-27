package gr.ratmole.android.Mach3Pendant;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import gr.ratmole.android.Mach3Pendant.model.Application;
import gr.ratmole.android.Mach3Pendant.model.Key;

public class HotkeysAdapter extends BaseAdapter {
    Application app;


    public void setApplication(Application app_) {
        app = app_;
        notifyDataSetChanged();
    }

    public int getCount() {
        return app == null ? 0 : app.keys.size();
    }

    public Key getItem(int i) {
        if (app == null) return null;
        try {
            return app.keys.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public long getItemId(int i) {
        if (app == null) return 0;
        try {
            return /*app.keys.get(i).id;*/ i;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hotkey, null);
        }

        TextView label = (TextView) view.findViewById(R.id.hotkey_label);


        //If app was set
        if (app != null) {
            try {
                Key key = app.keys.get(i);
                if (key.label.equalsIgnoreCase("Reset")){
                    label.setBackgroundColor(Color.RED);
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Cycle Start")){
                    label.setBackgroundColor(Color.GREEN);
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Feed Hold")){
                    label.setBackgroundColor(Color.YELLOW);
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Stop")){
                    label.setBackgroundColor(Color.GRAY);
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Home")){
                    label.setBackgroundColor(Color.GRAY);
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("X-")){
                    label.setBackgroundColor(Color.parseColor("#99ff99"));
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("X+")){
                    label.setBackgroundColor(Color.parseColor("#ccffcc"));
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Y-")){
                    label.setBackgroundColor(Color.parseColor("#99ffff"));
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Y+")){
                    label.setBackgroundColor(Color.parseColor("#ccffff"));
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Z-")){
                    label.setBackgroundColor(Color.parseColor("#9999ff"));
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("Z+")){
                    label.setBackgroundColor(Color.parseColor("#ccccff"));
                    label.setTextColor(Color.BLACK);
                }
                if (key.label.equalsIgnoreCase("blank")){
                    view.setVisibility(View.GONE);
                }
                label.setText(key.label);


                //if app have not full set keys
            } catch (IndexOutOfBoundsException e) {
                label.setText("");
            }
        } else {
            label.setText("");
        }


        return view;
    }

    private View.OnTouchListener transitionListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view_, MotionEvent motionEvent_) {
            TransitionDrawable dr = (TransitionDrawable) view_.getBackground();
            switch (motionEvent_.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dr.startTransition(500);
                    return true;
                case MotionEvent.ACTION_UP:
                    dr.reverseTransition(500);
                    break;
            }
            return false;
        }
    };

}
