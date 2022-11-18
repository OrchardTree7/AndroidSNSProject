// Generated by view binder compiler. Do not edit!
package com.example.snsproject.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.snsproject.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final EditText emailEditText;

  @NonNull
  public final Button emailSignInBtn;

  @NonNull
  public final Button emailSignUpBtn;

  @NonNull
  public final ImageView imageView;

  @NonNull
  public final EditText passwordEditText;

  @NonNull
  public final LinearLayout signinLayout;

  private ActivityLoginBinding(@NonNull ConstraintLayout rootView, @NonNull EditText emailEditText,
      @NonNull Button emailSignInBtn, @NonNull Button emailSignUpBtn, @NonNull ImageView imageView,
      @NonNull EditText passwordEditText, @NonNull LinearLayout signinLayout) {
    this.rootView = rootView;
    this.emailEditText = emailEditText;
    this.emailSignInBtn = emailSignInBtn;
    this.emailSignUpBtn = emailSignUpBtn;
    this.imageView = imageView;
    this.passwordEditText = passwordEditText;
    this.signinLayout = signinLayout;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.email_editText;
      EditText emailEditText = ViewBindings.findChildViewById(rootView, id);
      if (emailEditText == null) {
        break missingId;
      }

      id = R.id.email_signInBtn;
      Button emailSignInBtn = ViewBindings.findChildViewById(rootView, id);
      if (emailSignInBtn == null) {
        break missingId;
      }

      id = R.id.email_signUpBtn;
      Button emailSignUpBtn = ViewBindings.findChildViewById(rootView, id);
      if (emailSignUpBtn == null) {
        break missingId;
      }

      id = R.id.imageView;
      ImageView imageView = ViewBindings.findChildViewById(rootView, id);
      if (imageView == null) {
        break missingId;
      }

      id = R.id.password_editText;
      EditText passwordEditText = ViewBindings.findChildViewById(rootView, id);
      if (passwordEditText == null) {
        break missingId;
      }

      id = R.id.signin_layout;
      LinearLayout signinLayout = ViewBindings.findChildViewById(rootView, id);
      if (signinLayout == null) {
        break missingId;
      }

      return new ActivityLoginBinding((ConstraintLayout) rootView, emailEditText, emailSignInBtn,
          emailSignUpBtn, imageView, passwordEditText, signinLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
