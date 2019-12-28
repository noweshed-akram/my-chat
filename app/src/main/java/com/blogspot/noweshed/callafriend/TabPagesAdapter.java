package com.blogspot.noweshed.callafriend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.noweshed.callafriend.Fragments.CallsFragment;
import com.blogspot.noweshed.callafriend.Fragments.ChatsFragment;
import com.blogspot.noweshed.callafriend.Fragments.FriendsFragment;
import com.blogspot.noweshed.callafriend.Fragments.RequestFragment;

class TabPagesAdapter extends FragmentPagerAdapter {
    public TabPagesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int positions) {
        switch (positions) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                CallsFragment callsFragment = new CallsFragment();
                return callsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Calls";
            case 2:
                return "Friends";
            case 3:
                return "Request";
            default:
                return null;
        }
    }
}
