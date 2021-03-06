/*
    Park Catcher Montréal
    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    Copyright (C) 2012 Mudar Noufal <mn@mudar.ca>

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.parkcatcher.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SearchMessageHandler extends Handler {

    private final WeakReference<SearchHandlerCallbacks> mListener;

    /**
     * Caller must implement this interface to receive the handler's message
     */
    public interface SearchHandlerCallbacks {
        public void onSearchResults(Message msg);
    }

    public SearchMessageHandler(SearchHandlerCallbacks target) {
        mListener = new WeakReference<SearchHandlerCallbacks>(target);
    }

    @Override
    public void handleMessage(Message msg)
    {
        SearchHandlerCallbacks target = mListener.get();
        if (target != null) {
            target.onSearchResults(msg);
        }
    }
}
