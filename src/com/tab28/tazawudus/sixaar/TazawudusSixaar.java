package com.tab28.tazawudus.sixaar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TazawudusSixaar extends FragmentActivity implements
		EnteteFragment.OnHeadlineSelectedListener {

	int pos;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_articles);

		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first
		// fragment
		AlertDialog alertDialog1 = new AlertDialog.Builder(this).create();
		// Setting Dialog Title
		alertDialog1.setTitle("PRIX A PAYER");
		// Setting Dialog Message
		alertDialog1
				.setMessage(Html
						.fromHtml("<center>NOTRE OBJECTIF: Oeuvrer pour Cheikh Ahmadou Bamba Khadimou Rassoul. <br/>Nous demandons à toute personne utilisant ce logiciel de prier pour SERIGNE SALIOU MBACKE</center>"));
		// Setting Icon to Dialog
		alertDialog1.setIcon(R.drawable.serignesaliou);
		// Setting OK Button
		alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog1.show();
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create an instance of ExampleFragment
			EnteteFragment firstFragment = new EnteteFragment();
			findViewById(R.id.fragment_container).setBackgroundColor(
					Color.BLACK);
			// In case this activity was started with special instructions from
			// an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, firstFragment).commit();
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public void onArticleSelected(int position) {
		pos = position;
		// The user selected the headline of an article from the EnteteFragment
		// Capture the article fragment from the activity layout
		ContenuFragment articleFrag = (ContenuFragment) getSupportFragmentManager()
				.findFragmentById(R.id.article_fragment);

		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...

			// Call a method in the ContenuFragment to update its content
			articleFrag.updateArticleView(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it an argument for the selected article
			ContenuFragment newFragment = new ContenuFragment();
			Bundle args = new Bundle();
			args.putInt(ContenuFragment.ARG_POSITION, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	public void DownloadFile(String fileURL) {
		try {
			String folderName = getFolderName(fileURL);
			File root = Environment.getExternalStorageDirectory();
			File dir = new File(root.getAbsolutePath() + "/xamxam/"
					+ folderName);
			if (!dir.exists())
				dir.mkdirs();
			URL u = new URL(fileURL);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			String fileName = getFileName(fileURL);
			FileOutputStream f = new FileOutputStream(new File(dir, fileName));

			InputStream in = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (Exception e) {
			Log.d("Downloader", e.getMessage());
		}

	}

	public String fileToRead(String fileURL) {
		String path = null;
		try {
			String folderName = getFolderName(fileURL);
			String fileName = getFileName(fileURL);
			File root = Environment.getExternalStorageDirectory();
			path = root.getAbsolutePath() + "/xamxam/" + folderName + "/"
					+ fileName;

		} catch (Exception e) {
			Log.d("fileToRead", e.getMessage());
		}
		return path;
	}

	public boolean existFile(String fileURL) {
		boolean isExist = false;
		try {
			String folderName = getFolderName(fileURL);
			String fileName = getFileName(fileURL);
			File root = Environment.getExternalStorageDirectory();
			File dir = new File(root.getAbsolutePath() + "/xamxam/"
					+ folderName + "/" + fileName);
			if (dir.exists())
				isExist = true;
		} catch (Exception e) {
			Log.d("existFile", e.getMessage());
		}
		return isExist;
	}

	public String getFileName(String url) {
		int taille = url.length();
		int positionDernierSlash = url.lastIndexOf("/");
		String fileName = url.substring(positionDernierSlash + 1, taille);
		return fileName;
	}

	public String getFolderName(String url) {
		String folderName = null;
		int positionDernierSlash = url.lastIndexOf("/");
		String titreRep = url.substring(0, positionDernierSlash);
		System.out.println(titreRep);
		int i = titreRep.lastIndexOf("/");
		folderName = url.substring(i + 1, titreRep.length());
		return folderName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			openOptionsDialog();
			return true;
		case R.id.app_exit:
			exitOptionsDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void exitOptionsDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_exit)
				.setMessage(R.string.app_exit_message)
				.setNegativeButton(R.string.str_no,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						})
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								finish();
								System.exit(0);
							}
						}).show();
	}

	private void openOptionsDialog() {
		AboutDialog about = new AboutDialog(this);
		about.setTitle(Html.fromHtml(this.getString(R.string.app_about)));
		about.show();
	}
}