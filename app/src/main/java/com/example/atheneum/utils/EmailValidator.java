package com.example.atheneum.utils;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Does checking of emails for validity
 */
public class EmailValidator extends TextValidator {
    /**
     * Creates a new TextValidator object.
     *
     * @param textView
     */
    public EmailValidator(TextView textView) {
        super(textView);
    }

    /**
     * Validates the text contained within the text view and returns the result of the validation
     * procedure.
     *
     * @param textView TextView that we want to run validation on.
     * @return true if the text within the text view is valid and false
     * otherwise.
     */
    @Override
    public boolean validate(TextView textView) {
        // See: https://stackoverflow.com/a/21495067/11039833
        if (TextUtils.isEmpty(textView.getText())
                ||!android.util.Patterns.EMAIL_ADDRESS.matcher(textView.getText().toString()).matches()) {
            textView.setError("Invalid email address!");
            return false;
        }
        textView.setError(null);
        return true;
    }
}
