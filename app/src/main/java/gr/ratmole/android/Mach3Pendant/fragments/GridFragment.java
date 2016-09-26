package gr.ratmole.android.Mach3Pendant.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import gr.ratmole.android.Mach3Pendant.ConnectivityManager;
import gr.ratmole.android.Mach3Pendant.HotkeysAdapter;
import gr.ratmole.android.Mach3Pendant.Mach3PendantApplication;
import gr.ratmole.android.Mach3Pendant.R;
import gr.ratmole.android.Mach3Pendant.model.Application;
import gr.ratmole.android.Mach3Pendant.model.Hotkeys;
import gr.ratmole.android.Mach3Pendant.model.Key;
import gr.ratmole.android.Mach3Pendant.shared.Event;
import gr.ratmole.android.Mach3Pendant.shared.EventSequence;
import gr.ratmole.android.Mach3Pendant.shared.KeyEvent;
import gr.ratmole.android.Mach3Pendant.utils.Log;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 22.07.11
 * Time: 22:10
 * To change this template use File | Settings | File Templates.
 */
public class GridFragment extends Fragment {
    private Hotkeys hotkeys;
    private GridView grid;

    //Adapter for keys grid view
    private HotkeysAdapter hotkeysAdapter;
    private ConnectivityManager _connManager;
    private LayoutAnimationController gridAnimation;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mach3PendantApplication application = (Mach3PendantApplication) getActivity().getApplication();
        _connManager = application.getConnectivityManager();

    }
    @SuppressLint("ValidFragment")
    public GridFragment(Hotkeys hotkeys_) {
        this.hotkeys = hotkeys_;
        this.hotkeysAdapter = new HotkeysAdapter();
    }

    public GridFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.grid_fragment, container, false);
        grid = (GridView) v.findViewById(R.id.grid_of_keys);
        grid.setAdapter(hotkeysAdapter);
        hotkeysAdapter.setApplication(hotkeys.getActiveApp());


        grid.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent me) {



                int action = me.getActionMasked();

                if (action == MotionEvent.ACTION_DOWN) {
                    float currentXPosition = me.getX();
                    float currentYPosition = me.getY();
                    int position = grid.pointToPosition((int) currentXPosition, (int) currentYPosition);
                    Key key = hotkeysAdapter.getItem(position);

                    if (key != null) {
                        EventSequence msg  = key.getEventSequence();

                        if (key.shortcut.equalsIgnoreCase("Ctrl+X+0")){
                            try {
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_CONTROL));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_X));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_CONTROL));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_X));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_0));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_0));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_ENTER));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_ENTER));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //Log.e("x0");
                            return false;
                        }
                        else if (key.shortcut.equalsIgnoreCase("Ctrl+Y+0")){
                            try {
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_CONTROL));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_Y));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_CONTROL));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_Y));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_0));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_0));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_ENTER));
                                Thread.sleep(100);
                                _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_ENTER));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.e("y0");
                            return false;
                        }
                        else if (key.shortcut.equalsIgnoreCase("Ctrl+Z+0")){
                            try {
                            _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_CONTROL));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_Z));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_CONTROL));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_Z));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_0));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_0));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().press(KeyEvent.VK_ENTER));
                                Thread.sleep(100);
                            _connManager.sendMessage(new EventSequence().release(KeyEvent.VK_ENTER));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //Log.e("z0");
                            return false;
                        }
                        else {
                            for (Event event : msg.getSequence()) {
                                if (event instanceof KeyEvent) {
                                    KeyEvent keyEvent = (KeyEvent) event;
                                    if (keyEvent.press) {
                                        _connManager.sendMessage(new EventSequence().press(keyEvent.code));
                                        //Log.e("Press: " + keyEvent.code);
                                    }
                                }
                            }
                        }
                    }

                } else if (action == MotionEvent.ACTION_UP) {
                    float currentXPosition = me.getX();
                    float currentYPosition = me.getY();
                    int position = grid.pointToPosition((int) currentXPosition, (int) currentYPosition);
                    Key key = hotkeysAdapter.getItem(position);

                    if (key != null) {
                        EventSequence msg  = key.getEventSequence();
                        for (Event event : msg.getSequence()) {
                            if (event instanceof KeyEvent) {
                                KeyEvent keyEvent = (KeyEvent) event;
                                if (keyEvent.press) {
                                    _connManager.sendMessage(new EventSequence().release(keyEvent.code));
                                    //Log.e("Release: " +keyEvent.code);

                                }
                            }
                        }
                    }

                }
                return false;
            }

        });



        gridAnimation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_grid_inverse_fade);
        grid.setLayoutAnimation(gridAnimation);
        return v;
    }

    public void setActiveApp(Application activeApp) {
        hotkeysAdapter.setApplication(activeApp);

        //grid can be null while fragment creating?
        //[10.01.12 Comment to disable animation]
        /*if (grid != null) {
            grid.startLayoutAnimation();
        }*/
    }
}
