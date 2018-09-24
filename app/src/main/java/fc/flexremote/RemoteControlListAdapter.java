package fc.flexremote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class RemoteControlListAdapter extends ArrayAdapter<RemoteControlConfig> {

    private ArrayList<RemoteControlConfig> remoteControlConfigs;
    private Context c;

    // View lookup cache
    private static class ViewHolder {
        ImageView orientation;
        TextView remoteControlName;
        ImageButton edit;
        ImageButton delete;
    }

    public RemoteControlListAdapter(Context c, ArrayList<RemoteControlConfig> remoteControlConfigs) {
        super(c, R.layout.remote_control_list_item, remoteControlConfigs);
        this.remoteControlConfigs = remoteControlConfigs;
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RemoteControlConfig remoteControlConfig = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.remote_control_list_item, parent, false);
            viewHolder.orientation = (ImageView) convertView.findViewById(R.id.item_orientation);
            viewHolder.remoteControlName = (TextView) convertView.findViewById(R.id.item_keyboardName);
            viewHolder.edit = (ImageButton) convertView.findViewById(R.id.item_edit);
            viewHolder.delete = (ImageButton) convertView.findViewById(R.id.item_delete);

            // view lookup cache stored in tag
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (remoteControlConfig.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
            viewHolder.orientation.setBackgroundResource(R.drawable.ic_stay_primary_portrait_black_24dp);
        else
            viewHolder.orientation.setBackgroundResource(R.drawable.ic_stay_primary_landscape_black_24dp);

        viewHolder.remoteControlName.setText(remoteControlConfig.getName());

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPopUpMenu(v);
            }
        });

        viewHolder.edit.setTag(position);

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showDeleteDialog(v);
            }
        });

        viewHolder.delete.setTag(position);

        return convertView;
    }

    private boolean isNameExist(String name) {
        File f = new File(c.getFilesDir(), name);
        return f.exists();
    }

    private void renameOnValidName(String newRemoteControlName, RemoteControlConfig remoteControlConfig) {
        if (newRemoteControlName.equals("")) {
            Toast.makeText(c, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else if (isNameExist(newRemoteControlName)) {
            Toast.makeText(c, "Name already exist", Toast.LENGTH_SHORT).show();
        }
        else if (!newRemoteControlName.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(c, "Name can only consist of alphabets and digits", Toast.LENGTH_SHORT).show();
        }
        else {
            File renameFrom = new File(c.getFilesDir(), remoteControlConfig.getName());
            File renameTo = new File(c.getFilesDir(), newRemoteControlName);
            renameFrom.renameTo(renameTo);
            remoteControlConfig.setName(newRemoteControlName);
            notifyDataSetChanged();
        }
    }

    private void showRenameDialog(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.DialogTheme);
        builder.setMessage("Remote control name:");

        final EditText input = new EditText(c);
        input.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.MATCH_PARENT));
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String enteredName = input.getText().toString();
                int position = (int) v.getTag();
                RemoteControlConfig remoteControlConfig = getItem(position);
                renameOnValidName(enteredName, remoteControlConfig);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing and let the dialog goes
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPopUpMenu(final View anchor) {
        PopupMenu popup = new PopupMenu(c, anchor);
        popup.getMenuInflater().inflate(R.menu.popup_menu_edit_button, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Rename")) {
                    showRenameDialog(anchor);
                }
                else {
                    int position = (int) anchor.getTag();
                    RemoteControlConfig remoteControlConfig = getItem(position);
                    Intent startConfigureKeyboardActivity = new Intent(c, ConfigureRemoteControlActivity.class);
                    startConfigureKeyboardActivity.putExtra("purpose", "edit");
                    startConfigureKeyboardActivity.putExtra("remote_name", remoteControlConfig.getName());
                    c.startActivity(startConfigureKeyboardActivity);
                }
                return true;
            }
        });

        popup.show();
    }

    private void showDeleteDialog(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.DialogTheme);
        builder.setMessage("Delete this custom remote?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int position = (int) v.getTag();
                RemoteControlConfig remoteControlConfig = getItem(position);

                File keyboardConfigFile = new File(c.getFilesDir(), remoteControlConfig.getName());
                keyboardConfigFile.delete();
                remoteControlConfigs.remove(remoteControlConfig);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
