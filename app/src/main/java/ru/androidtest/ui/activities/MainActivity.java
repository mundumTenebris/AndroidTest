package ru.androidtest.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.androidtest.R;
import ru.androidtest.dataBaseLayer.ContractClass.LinkedListTable;
import ru.androidtest.dataProviders.AbstractDataProvider;
import ru.androidtest.dataProviders.AbstractExpandableDataProvider;
import ru.androidtest.handlers.SFBaseCommand;
import ru.androidtest.handlers.impl.CRUDCommand.QUERYCommand;
import ru.androidtest.handlers.impl.CRUDCommand.UPDATECommand;
import ru.androidtest.interactionIterface.OnInteractionListener;
import ru.androidtest.sfClasses.SFBaseActivity;
import ru.androidtest.ui.fragments.DataProviderFragment;
import ru.androidtest.ui.fragments.RecyclerListViewFragment;

public class MainActivity extends SFBaseActivity implements OnInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            addRetainFragment(DataProviderFragment.newInstance(), getString(R.string.retain_fragment));
            pushFragments(RecyclerListViewFragment.newInstance(), false, false, getString(R.string.list_view_fragment));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_expandable) {
            Intent intent = new Intent(this, ExpandableActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public AbstractExpandableDataProvider getExpandableDataProvider() {
        return null;
    }

    @Override
    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.retain_fragment));
        return ((DataProviderFragment) fragment).getDataProvider();
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);

        if (getServiceHelper().check(requestIntent, QUERYCommand.class)) {
            if (resultCode == SFBaseCommand.RESPONSE_SUCCESS) {
                getDataProvider().setMData(requestId, requestIntent, resultCode, resultData);
                getContentResolver().notifyChange(LinkedListTable.CONTENT_QUERY_URI, null);
            }
        }
        if (getServiceHelper().check(requestIntent, UPDATECommand.class)) {
            //
        }
    }

    private void pushFragments(Fragment fragment, boolean shouldAnimate, boolean addBackS, String _tag) {
        FragmentManager     manager = getSupportFragmentManager();
        FragmentTransaction ft      = manager.beginTransaction();

        if (shouldAnimate) {
            //Custom animation
        }

        ft.replace(R.id.fragment_container, fragment, _tag);

        if (addBackS) {
            ft.addToBackStack(getString(R.string.back_stack));
        }

        ft.commit();
    }

    private void addRetainFragment(Fragment fragment, String _tag) {
        FragmentManager     manager = getSupportFragmentManager();
        FragmentTransaction ft      = manager.beginTransaction();
        ft.add(fragment, _tag);
        ft.commit();
    }
}
