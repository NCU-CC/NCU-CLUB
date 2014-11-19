package evan.ncu.club;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;

public class MainActivity extends ActionBarActivity{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
    private ListView mDrawerListView;
    private int mCurrentSelectedPosition = 0;
    private SwingLeftInAnimationAdapter mAnimAdapter;
    private static final int INITIAL_DELAY_MILLIS = 500;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = getTitle();

		// Set up the drawer.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            // 實作 drawer toggle 並放入 toolbar
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
                @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerListView.setVisibility(View.GONE);
                super.onDrawerClosed(drawerView);
                restoreActionBar();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                restoreActionBar();
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        initDrawer();
        onNavigationDrawerItemSelected(0);
        getSupportActionBar().setTitle(getString(R.string.title_section1));
	}

	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new GeneralBoard();
		FragmentManager fragmentManager = getFragmentManager();
		
		 switch(position) {
	        case 0:
                fragment = new LatestEvent();
	            break;
	        case 1:
                fragment = new GeneralBoard();
	            break;
	        case 2:
	            fragment = new LatestSchedule();
	            break;
	        
	    }
	    fragmentManager.beginTransaction()
	        .replace(R.id.container, fragment)
	        .commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 0:
			mTitle = getString(R.string.title_section1);
			break;
		case 1:
			mTitle = getString(R.string.title_section2);
			break;
		case 2:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListView);
        if(drawerOpen){

            mAnimAdapter = new SwingLeftInAnimationAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1, new String[] {
                    getString(R.string.title_section1),
                    getString(R.string.title_section2),
                    getString(R.string.title_section3), }));

            mAnimAdapter.setAbsListView(mDrawerListView);
            assert mAnimAdapter.getViewAnimator() != null;
            mAnimAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
            mDrawerListView.setAdapter(mAnimAdapter);
            if (mCurrentSelectedPosition >= 0) {
                mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
            }

        }
        else{
            mDrawerListView.setAdapter(null);
        }
        return super.onPrepareOptionsMenu(menu);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		if (!mNavigationDrawerFragment.isDrawerOpen()) {
//			// Only show items in the action bar relevant to this screen
//			// if the drawer is not showing. Otherwise, let the drawer
//			// decide what to show in the action bar.
//			getMenuInflater().inflate(R.menu.main, menu);
//			restoreActionBar();
//			return true;
//		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

    public void initDrawer(){
        mDrawerListView = (ListView) findViewById(R.id.drawer_list);
        mDrawerListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectItem(position);
                    }
                });
    }
    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerListView);
        }
        onNavigationDrawerItemSelected(position);
    }
}
