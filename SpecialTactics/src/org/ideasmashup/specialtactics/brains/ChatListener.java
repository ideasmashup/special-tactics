package org.ideasmashup.specialtactics.brains;

import bwapi.Player;

public interface ChatListener {

	public abstract void onSendText(String text);

	public abstract void onReceiveText(Player player, String text);

}
