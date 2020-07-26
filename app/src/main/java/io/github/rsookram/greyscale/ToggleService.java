package io.github.rsookram.greyscale;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class ToggleService extends TileService {

    @Override
    public void onClick() {
        super.onClick();

        if (!Util.hasPermission(this)) {
            Toast.makeText(this, R.string.no_permission, Toast.LENGTH_LONG).show();
            return;
        }

        int oldState = getQsTile().getState();
        if (oldState == Tile.STATE_ACTIVE) {
            setState(Tile.STATE_INACTIVE);
        } else {
            setState(Tile.STATE_ACTIVE);
        }

        Util.toggleGreyscale(this, oldState == Tile.STATE_INACTIVE);
    }

    private void setState(int state) {
        Tile tile = getQsTile();
        tile.setState(state);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        boolean greyscaleEnable = Util.isGreyscaleEnable(this);
        setState(greyscaleEnable ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
    }
}
