package com.example.atheneum.utils;

import android.widget.EditText;

/**
 * Small class to aggregate an EditText with a TextValidator to
 * allow manually calling the validator when needed, ie. when a user
 * tries to save changes.
 */
public final class EditTextWithValidator {
    public EditText editText;
    public TextValidator validator;

    public EditTextWithValidator(EditText editText, TextValidator validator) {
        this.editText = editText;
        this.validator = validator;
    }

}
