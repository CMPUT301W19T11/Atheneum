package com.example.atheneum.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

// Obtained from StackOverflow: https://stackoverflow.com/a/11838715
// License: https://creativecommons.org/licenses/by-sa/3.0/

/**
 * Class that is used to validate changes to a TextView as the text within the TextView changes.
 * An instance of this class is used as a TextChangedListener for the TextView.
 *
 * Obtained from StackOverflow: https://stackoverflow.com/a/11838715
 * License: https://creativecommons.org/licenses/by-sa/3.0/
 */

public abstract class TextValidator implements TextWatcher {
    private final TextView textView;

    /**
     * Creates a new TextValidator object.
     *
     * @param textView
     */
    public TextValidator(TextView textView) {
        this.textView = textView;
    }

    /**
     * Validates the text contained within the text view and returns the result of the validation
     * procedure.
     *
     * @param textView TextView that we want to run validation on.
     * @return true if the text within the text view is valid and false
     *         otherwise.
     */
    public abstract boolean validate(TextView textView);

    /**
     * Callback that validates the text within the TextView se are interested in after text has
     * changed.
     *
     * @param s Editable object that we are running validation on.
     */
    @Override
    final public void afterTextChanged(Editable s) {
        validate(textView);
    }

    /**
     * Callback that handles events before the text within the TextView has changed.
     *
     * Note: This method is not actually used since we are only interested in running validation
     *       after the text has changed.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    /**
     * Callback that handles events that happen while the text within the TextView is changing.
     *
     * Note: This method is not actually used since we are only interested in running validation
     *       after the text has changed.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }

}
