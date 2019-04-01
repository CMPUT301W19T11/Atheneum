package com.example.atheneum.utils;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Checks if an field is empty and displays a warning
 */
public class NonEmptyTextValidator extends TextValidator {

    /**
     * Instantiates a new Non empty text validator.
     *
     * @param textView the text view
     */
    public NonEmptyTextValidator(TextView textView) {
        super(textView);
    }

    @Override
    public boolean validate(TextView textView) {
        if (TextUtils.isEmpty(textView.getText())) {
            textView.setError("Field cannot be left blank!");
            return false;
        }

        return true;
    }
}
