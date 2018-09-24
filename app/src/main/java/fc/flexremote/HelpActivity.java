package fc.flexremote;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_help);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    TextView serverDownloadLink = (TextView) findViewById(R.id.faq2_server_dl_link);
    serverDownloadLink.setClickable(true);
    serverDownloadLink.setMovementMethod(LinkMovementMethod.getInstance());
    String html = "<a href='https://drive.google.com/file/d/1R1BYQuq4KRMmm_Og1rS6VTh-F9mB4m7U/view?usp=sharing'>FlexRemote Server</a>";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      serverDownloadLink.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
    else
      serverDownloadLink.setText(Html.fromHtml(html));
  }

}
