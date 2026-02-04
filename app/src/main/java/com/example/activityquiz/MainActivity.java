package com.example.activityquiz;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private boolean[] mAnswered;
    private int mScore = 0;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_SCORE = "score";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_CHEATER = "cheater";
    private static final String KEY_CHEATED = "cheated";


    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private com.example.activityquiz.Question[] mQuestionBank = new com.example.activityquiz.Question[] {
            new com.example.activityquiz.Question(R.string.question_australia, true),
            new com.example.activityquiz.Question(R.string.question_oceans, true),
            new com.example.activityquiz.Question(R.string.question_mideast, false),
            new com.example.activityquiz.Question(R.string.question_africa, false),
            new com.example.activityquiz.Question(R.string.question_americas, true),
            new com.example.activityquiz.Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);


        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mScore = savedInstanceState.getInt(KEY_SCORE, 0);
            mAnswered = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            boolean[] cheated = savedInstanceState.getBooleanArray(KEY_CHEATED);
            if (cheated != null) {
                for (int i = 0; i < cheated.length; i++) {
                    mQuestionBank[i].setCheated(cheated[i]);
                }
            }
        } else {
            mAnswered = new boolean[mQuestionBank.length];
        }


        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.previous_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length)
                        % mQuestionBank.length;
                updateQuestion();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);

                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_CHEAT || resultCode != Activity.RESULT_OK) {
            return;
        }

        if (data != null && CheatActivity.wasAnswerShown(data)) {
            mQuestionBank[mCurrentIndex].setCheated(true);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        boolean[] cheated = new boolean[mQuestionBank.length];
        savedInstanceState  .putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_CHEATED, cheated);
        savedInstanceState.putInt(KEY_SCORE, mScore);
        savedInstanceState.putBooleanArray(KEY_ANSWERED, mAnswered);


        for (int i = 0; i < mQuestionBank.length; i++) {
            cheated[i] = mQuestionBank[i].isCheated();
        }

        savedInstanceState.putBooleanArray(KEY_CHEATER, cheated);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        boolean answered = mAnswered[mCurrentIndex];
        mTrueButton.setEnabled(!answered);
        mFalseButton.setEnabled(!answered);
    }

    private void checkIfQuizFinished() {
        for (boolean answered : mAnswered) {
            if (!answered) {
                return;
            }
        }

        int percentage = (int) ((mScore * 100.0f) / mQuestionBank.length);

        Toast.makeText(
                this,
                "Your score: " + percentage + "%",
                Toast.LENGTH_LONG
        ).show();
    }

    private void checkAnswer(boolean userPressedTrue) {

        if (mAnswered[mCurrentIndex]) {
            return;
        }
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId;

        if (mQuestionBank[mCurrentIndex].isCheated()) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mScore++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        mAnswered[mCurrentIndex] = true;

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);

        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();

        checkIfQuizFinished();
    }





}