package Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import Controller.Api;
import Controller.DataFromApi;
import Models.UserResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.ConfirmAccountActivity;
import com.shahbaapp.lft.R;

public class AccountActivity extends Fragment {

    private EditText userName, password;
    private TextView login;
    private Api api;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_account, container, false);



        userName = view.findViewById(R.id.user_name);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);

        api = DataFromApi.getApi();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter your username", Toast.LENGTH_LONG).show();

                else if (password.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_LONG).show();

                else {
                    hideKeyboard();

                    Call<UserResult> call = api.SignIn(userName.getText().toString().trim(), password.getText().toString());

                    call.enqueue(new Callback<UserResult>() {
                        @Override
                        public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                            UserResult userResult = response.body();
                            if(userResult.results != null){
                                Intent i = new Intent(getActivity(), ConfirmAccountActivity.class);
                                i.putExtra("userId", userResult.results.id);
                                i.putExtra("fullName", userResult.results.fullName);
                                i.putExtra("image", userResult.results.profileImage);
                                startActivity(i);

                            }else
                                Toast.makeText(getContext(),userResult.status,Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onFailure(Call<UserResult> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        return view;

    }


    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}