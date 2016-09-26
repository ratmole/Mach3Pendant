package gr.ratmole.android.Mach3Pendant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import gr.ratmole.android.Mach3Pendant.R;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 22.07.11
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionFragment extends Fragment {
    private TextView lblHelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.connection_fragment, container, false);
        lblHelp = (TextView) v.findViewById(R.id.lbl_connection_help);

        return v;
    }

}
