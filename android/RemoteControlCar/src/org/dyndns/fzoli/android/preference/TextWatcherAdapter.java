package org.dyndns.fzoli.android.preference;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Kódrövidítés céljából létrehozott osztály.
 * Implementálja a TextWatcher interfész összes metódusát.
 * Az összes metódus semmittevő.
 */
public abstract class TextWatcherAdapter implements TextWatcher {

	@Override
	public void afterTextChanged(Editable s) {
		;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		;
	}

}
