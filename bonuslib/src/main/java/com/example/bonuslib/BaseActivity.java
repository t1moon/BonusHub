package com.example.bonuslib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*To store fragments of all menus/tabs, we need to use Map object as container.
Each element of it consists of a pair of key and value in which key is type of menu/tab and value
is stack of fragments. We will push and pop the fragment into corresponding stack.*/

public class BaseActivity extends AppCompatActivity {
    public Map<FragmentType, Stack<Fragment>> mStackMap;
    protected FragmentType mCurrentBaseFragment = null;
    private static StackListner stackListner = null;

    public static void setStackListner(StackListner listner) {
        stackListner = listner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStackFragments();
    }

    private void initStackFragments() {
        mStackMap = new HashMap<FragmentType, Stack<Fragment>>();
        for (FragmentType fragmentType : FragmentType.values()) {
            mStackMap.put(fragmentType, new Stack<Fragment>());
        }
    }

    public int getCurrentStackSize() {
        return mStackMap.get(mCurrentBaseFragment).size();
    }

    public void popWholeStack() {
        final Stack<Fragment> stackFragment = mStackMap.get(mCurrentBaseFragment);
        while(stackFragment.size() > 2)
            stackFragment.pop();
        popFragment();
    }

    public void setCurrentFragment(FragmentType menuType) {
        mCurrentBaseFragment = menuType;
    }

    public FragmentType getCurrentFragment() {
        return mCurrentBaseFragment;
    }

    protected int getFragmentContainerResId() {
        return 0;
    }

    public void pushFragment(Fragment fragment, boolean shouldAdd) {
        if (shouldAdd) {
            mStackMap.get(mCurrentBaseFragment).push(fragment);
            if (getCurrentStackSize() > 1)
                stackListner.deepStack();
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(getFragmentContainerResId(), fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    public void pushFragment(Fragment fragment, boolean shouldAdd, Bundle argBundle) {
        if (shouldAdd) {
            mStackMap.get(mCurrentBaseFragment).push(fragment);
            if (getCurrentStackSize() > 1)
                stackListner.deepStack();
        }
        if (argBundle != null) {
            fragment.setArguments(argBundle);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(getFragmentContainerResId(), fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    public void popFragment() {
        final Stack<Fragment> stackFragment = mStackMap.get(mCurrentBaseFragment);
        if (stackFragment.size() > 1) {
            Fragment fragment = stackFragment.elementAt(stackFragment.size() - 2);  // -1 for previous and -1 for index
            stackFragment.pop();
            if (getCurrentStackSize() < 2)
                stackListner.homeStack();
            if (fragment != null) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ft.replace(getFragmentContainerResId(), fragment, fragment.getClass().getSimpleName());
                ft.commit();
            }
        }
    }

    public void popFragment(Bundle argBundle) {
        final Stack<Fragment> stackFragment = mStackMap.get(mCurrentBaseFragment);
        if (stackFragment.size() > 1) {
            Fragment fragment = stackFragment.elementAt(stackFragment.size() - 2);
            stackFragment.pop();
            if (getCurrentStackSize() < 2)
                stackListner.homeStack();
            if (fragment != null) {
                if (argBundle != null) {
                    fragment.setArguments(argBundle);
                }
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ft.replace(getFragmentContainerResId(), fragment, fragment.getClass().getSimpleName());
                ft.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mStackMap.get(mCurrentBaseFragment).size() <= 1) {
            super.onBackPressed(); // or call finish..
        } else {
            popFragment();
        }
    }



}
