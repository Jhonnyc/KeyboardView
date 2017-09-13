package com.yonikal.keyboardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by yoni on 13/09/2017.
 */
public class KeyboardView extends LinearLayout {

    public final static String DEL_CLICK = "DEL";
    private static final int NO_VALUE = -1;

    private static final int KEYBOARD_TYPE_NUMBER = 0;
    private static final int KEYBOARD_TYPE_DECIMAL = 1;

    private final String TAG = KeyboardView.class.getSimpleName();
    /***********************************
     * **********************************
     * ******** Private Methods *********
     * **********************************
     ***********************************/

    // Class UI views
    private TextView mOne;
    private TextView mTwo;
    private TextView mThree;
    private TextView mFour;
    private TextView mFive;
    private TextView mSix;
    private TextView mSeven;
    private TextView mEight;
    private TextView mNine;
    private TextView mZero;
    private TextView mDot;
    private View mFinger;
    private LinearLayout mDelete;
    private LinearLayout mContainer;
    private TextView mTextView;
    private ArrayList<Tuple<TextView, Integer>> mTextViewList;
    private ArrayList<TextView> mKeyboardTextViews = new ArrayList<>();

    // Class parameters
    private boolean mAddCommas;
    private boolean mHasAttrs = false;
    private boolean mIsBold;
    private int mTxtFont;
    private int mBgColor;
    private float mTextSize = -1;
    private int mColor;
    private boolean mHintDisplay;
    private boolean mSecretMode;
    private int mRootIndex = 0;
    private int mInnerIndex = 0;
    private String mHint;
    private int mHintColor;
    private int mKeyboardType = KEYBOARD_TYPE_NUMBER;
    private ICustomKeyPress mICustomKeyPress;
    private OnClickListener mTextOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTextViewList != null) {
                if (mRootIndex <= mTextViewList.size() && mInnerIndex < mTextViewList.get(mRootIndex).size) {
                    onKeyClick(v);
                    if (mTextViewList != null && mTextViewList.get(mRootIndex).size > mInnerIndex) {
                        mInnerIndex++;
                        if (mRootIndex != (mTextViewList.size() - 1) && mInnerIndex == mTextViewList.get(mRootIndex).size) {
                            mRootIndex++;
                            mInnerIndex = 0;
                            bindTo(mTextViewList.get(mRootIndex).textView);
                        }
                    }
                }
            } else {
                onKeyClick(v);
            }
        }
    };

    /****************
     * *** Ctor's ****
     ****************/

    public KeyboardView(Context context) {
        super(context);
        initializeViews(context);
    }


    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHasAttrs = true;
        initStyles(context, attrs);
        initializeViews(context);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHasAttrs = true;
        initStyles(context, attrs);
        initializeViews(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContainer = (LinearLayout) findViewById(R.id.container);
        mOne = (TextView) findViewById(R.id.one);
        mKeyboardTextViews.add(mOne);
        mTwo = (TextView) findViewById(R.id.two);
        mKeyboardTextViews.add(mTwo);
        mThree = (TextView) findViewById(R.id.three);
        mKeyboardTextViews.add(mThree);
        mFour = (TextView) findViewById(R.id.four);
        mKeyboardTextViews.add(mFour);
        mFive = (TextView) findViewById(R.id.five);
        mKeyboardTextViews.add(mFive);
        mSix = (TextView) findViewById(R.id.six);
        mKeyboardTextViews.add(mSix);
        mSeven = (TextView) findViewById(R.id.seven);
        mKeyboardTextViews.add(mSeven);
        mEight = (TextView) findViewById(R.id.eight);
        mKeyboardTextViews.add(mEight);
        mNine = (TextView) findViewById(R.id.nine);
        mKeyboardTextViews.add(mNine);
        mZero = (TextView) findViewById(R.id.zero);
        mKeyboardTextViews.add(mZero);
        mDot = (TextView) findViewById(R.id.dot);
        mKeyboardTextViews.add(mDot);
        mDelete = (LinearLayout) findViewById(R.id.delete);
        mFinger = findViewById(R.id.finger);

        assignClickListener();

        if (mHasAttrs) {
            setValues();
        }
    }

    /***********************************
     * **********************************
     * ********* Class Methods **********
     * **********************************
     ***********************************/

    private void onKeyClick(final View v) {
        if (mTextView != null) {

            if (mHint != null && mHintDisplay && checkValid(((TextView) v).getText().toString())) {
                mTextView.setText(addComma(((TextView) v).getText().toString()));
                mTextView.setTextColor(mColor);
                mHintDisplay = false;
            } else {
                if (checkValid(mTextView.getText().toString() + ((TextView) v).getText().toString())) {
                    if (mAddCommas) {
                        mTextView.setText(addComma(mTextView.getText().toString() + ((TextView) v).getText().toString()));
                    } else {
                        final TextView textView = mTextView;
                        textView.setText(textView.getText().toString() + ((TextView) v).getText().toString());
//                        if (mSecretMode) {
//                            AnimationUtilities.pinCodeHidden(textView);
//                        }
                    }
                }
            }

            if (mICustomKeyPress != null && mTextView.getText().toString().length() == 1) {
                mICustomKeyPress.firstKeyEnter();
            }
        }

        if (v != null && mICustomKeyPress != null) {
            mICustomKeyPress.keyClick(((TextView) v).getText().toString());
        }
    }

    private void onDeleteKey(View v) {
        if (mTextView != null && !mHintDisplay) {
            String text = mTextView.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                text = text.substring(0, text.length() - 1);
                mTextView.setText(text);
            }

            if (mAddCommas) {
                mTextView.setText(addComma(mTextView.getText().toString()));
            } else {
                mTextView.setText(mTextView.getText().toString());
            }

            if (mTextView.getText().toString().length() == 0) {
                if (mHint != null) {
                    mTextView.setText(mHint);
                    mTextView.setTextColor(mHintColor);
                    mHintDisplay = true;

                }
                if (mICustomKeyPress != null) {
                    mICustomKeyPress.lastKeyDeleted();
                }
            }

        }
        if (v != null && mICustomKeyPress != null) {
            mICustomKeyPress.keyClick(DEL_CLICK);
        }
    }

    private boolean checkValid(String s) {
        s = s.replace("\u20AA", "");
        s = s.replace(",", "");
        if (s.startsWith(".")) {
            return true;
        }

        int dotCounter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                dotCounter++;
            }
        }
        if (dotCounter == 1 && s.endsWith(".")) {
            return true;
        }

        String regExp = "[0-9]+([,.][0-9]{1,2})?";
        return s.matches(regExp);

    }

    private String addComma(String text) {
        text = text.replace("\u20AA", "");

        text = text.replaceAll(",", "");

        if (text.startsWith(".")) {
            return 0 + ".";
        }


        int dotCounter = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '.') {
                dotCounter++;
            }
        }
        try {

            if (dotCounter == 1) {
                String[] dot = text.split("\\.");
                double number = Double.parseDouble(dot[0]);
                DecimalFormat commasFormat = new DecimalFormat("#,###");
                if (dot.length == 2) {
                    text = commasFormat.format(number) + "." + dot[1];
                } else {
                    text = commasFormat.format(number) + ".";
                }
            } else {
                double number = Double.parseDouble(text);
                DecimalFormat commasFormat = new DecimalFormat("#,###");
                text = commasFormat.format(number);
            }
        } catch (Exception e) {
            return text;
        }
        return text;
    }

    /***********************************
     * **********************************
     * ******** Public Methods *********
     * **********************************
     ***********************************/

    public void setCustomKeyPressListener(ICustomKeyPress iCustomKeyPress) {
        mICustomKeyPress = iCustomKeyPress;
    }

    public void assignClickListener() {
        for (TextView v : mKeyboardTextViews) {
            v.setOnClickListener(mTextOnClick);
        }

        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextViewList != null /*&& ((mRootIndex > 0 && mRootIndex < (mTextViewList.size()-1))
                || (mRootIndex == (mTextViewList.size()-1) && TextUtils.isEmpty(mTextView.getText())))*/) {
                    if (mInnerIndex != 0)
                        mInnerIndex--;
                    if (mTextViewList.get(mRootIndex).textView.length() == 0 && mRootIndex > 0) {
                        mRootIndex--;
                        mInnerIndex = mTextViewList.get(mRootIndex).textView.length() - 1;
                        bindTo(mTextViewList.get(mRootIndex).textView);
                    }


                }
                onDeleteKey(v);
            }
        });
    }

    public void removeClickListener() {
        for (TextView v : mKeyboardTextViews) {
            v.setClickable(false);
            v.setOnClickListener(null);
        }
        mDelete.setClickable(false);
        mDelete.setOnClickListener(null);

        mFinger.setClickable(false);
        mFinger.setOnClickListener(null);
    }

    public void bindTo(ArrayList<Tuple<TextView, Integer>> textViews) {
        mTextViewList = textViews;
        bindTo(mTextViewList.get(mRootIndex).textView);
        mInnerIndex = mTextViewList.get(mRootIndex).textView.getText().length();
    }

    public void bindTo(ArrayList<Tuple<TextView, Integer>> textViews, boolean isSecretMode) {
        mTextViewList = textViews;
        mSecretMode = isSecretMode;
        bindTo(mTextViewList.get(mRootIndex).textView);
        mInnerIndex = mTextViewList.get(mRootIndex).textView.getText().length();
    }

    public void reset(ArrayList<Tuple<TextView, Integer>> textViews, boolean pinReset) {
        mTextViewList = textViews;
        if (pinReset) {
            mRootIndex = 0;
            bindTo(mTextViewList.get(mRootIndex).textView);
            mInnerIndex = 0;
        } else {
            mRootIndex = mTextViewList.size() - 1;
            bindTo(mTextViewList.get(mRootIndex).textView);
            mInnerIndex = mTextViewList.get(mRootIndex).textView.getText().length();
        }
    }

    public void bindTo(TextView view) {
        mTextView = view;
        if (mHint != null) {
            mTextView.setText(mHint);
            if (mHintColor != NO_VALUE) {
                mTextView.setTextColor(mHintColor);
            }
            mHintDisplay = true;
        }
    }

    public void bindTo(TextView view, int viewIndex) {
        mTextView = view;
        mInnerIndex = viewIndex;
        mRootIndex = viewIndex - 1;
        if (mHint != null) {
            mTextView.setText(mHint);
            if (mHintColor != NO_VALUE) {
                mTextView.setTextColor(mHintColor);
            }
            mHintDisplay = true;
        }
    }

    public void bindTo(TextView view, boolean addCommas) {
        bindTo(view);
        mAddCommas = addCommas;
    }

    public void bindTo(TextView view, final ICustomKeyPress iCustomKeyPress) {
        bindTo(view);
        mICustomKeyPress = iCustomKeyPress;
    }

    public void bindTo(TextView view, final ICustomKeyPress iCustomKeyPress, boolean addCommas) {
        bindTo(view, iCustomKeyPress);
        mAddCommas = addCommas;
    }

    private void initStyles(Context context, AttributeSet attrs) {
        TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView);

        try {
            mBgColor = typedArray.getColor(R.styleable.KeyboardView_bgColor, NO_VALUE);
            mTextSize = typedArray.getDimension(R.styleable.KeyboardView_textSize, NO_VALUE);
            mColor = typedArray.getColor(R.styleable.KeyboardView_textColor, NO_VALUE);
            mHint = typedArray.getString(R.styleable.KeyboardView_textHint);
            mHintColor = typedArray.getColor(R.styleable.KeyboardView_textHintColor, NO_VALUE);
            mIsBold = typedArray.getBoolean(R.styleable.KeyboardView_boldText, false);
            mKeyboardType = typedArray.getInt(R.styleable.KeyboardView_keyboardType, KEYBOARD_TYPE_NUMBER);
            mTxtFont = -1;//typedArray.getInt(R.styleable.CustomKeyboard_font, -1);
        } finally {
            typedArray.recycle();
        }
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_keyboard, this);
    }

    private void setValues() {
        if (mBgColor != NO_VALUE) {
            mContainer.setBackgroundColor(mBgColor);
        }

        switch(mKeyboardType){
            case KEYBOARD_TYPE_DECIMAL:
                mFinger.setClickable(false);
                mFinger.setVisibility(GONE);
                mDot.setClickable(true);
                mDot.setVisibility(VISIBLE);
                break;
            default:
                mFinger.setClickable(false);
                mFinger.setVisibility(GONE);
                mDot.setClickable(false);
                mDot.setVisibility(INVISIBLE);
                break;
        }

        //setTextViewsStyles(mContainer, mTextSize, mColor);
    }

//    private void setTextViewsStyles(ViewGroup view, float size, int color) {
//        for (int i = 0; i < view.getChildCount(); i++) {
//            View v = view.getChildAt(i);
//            if (v instanceof TextView) {
//                if (size > -1) {
//                    ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
//                }
//                if (color != NO_VALUE) {
//                    ((TextView) v).setTextColor(mColor);
//                }
//                if (mIsBold && FontText.isValidEnum(mTxtFont)) {
//                    Typeface face = FontManager.get(FontText.nameFromId(mTxtFont), getContext());
//                    ((TextView) v).setTypeface(face, Typeface.BOLD);
//                } else if (FontText.isValidEnum(mTxtFont)) {
//                    FontManager.setCustomFont(((FontableTextView) v), FontText.nameFromId(mTxtFont), getContext());
//                } else if (mIsBold) {
//                    ((TextView) v).setTypeface(null, Typeface.BOLD);
//                }
//            } else if (v instanceof ViewGroup) {
//                setTextViewsStyles((ViewGroup) v, mTextSize, mColor);
//            }
//        }
//    }

    public interface ICustomKeyPress {
        void firstKeyEnter();

        void lastKeyDeleted();

        void textLength(int length);

        void keyClick(String text);
    }

    public static class CustomKeyPressAdapter implements ICustomKeyPress {
        @Override
        public void firstKeyEnter() {
        }

        @Override
        public void lastKeyDeleted() {
        }

        @Override
        public void textLength(int length) {
        }

        @Override
        public void keyClick(String text) {
        }
    }

    public static class Tuple<X, Y> {
        public final X textView;
        public final Y size;

        public Tuple(X textView, Y size) {
            this.textView = textView;
            this.size = size;
        }
    }
}
