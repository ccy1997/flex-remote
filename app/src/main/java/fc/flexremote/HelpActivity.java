package fc.flexremote;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * This activity represents the FAQ screen
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class HelpActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);

    // Set toolbar attributes
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_help);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    // Set PC server download link attributes
    TextView serverDownloadLink = (TextView) findViewById(R.id.faq2_server_dl_link);
    serverDownloadLink.setClickable(true);
    serverDownloadLink.setMovementMethod(LinkMovementMethod.getInstance());
    String html = "<a href='https://github.com/ccy1997/flex-remote/releases'>FlexRemote Server</a>";

    // For different android versions
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      serverDownloadLink.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
    else
      serverDownloadLink.setText(Html.fromHtml(html));
  }

}
