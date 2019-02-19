package shahbasoft.lft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.orm.SugarRecord;

import Activity.AccountActivity;
import Activity.AddHomeworkActivity;
import Activity.AddNewsActivity;
import Activity.ExamStudentActivity;
import Activity.ExploreActivity;
import Activity.HomeworkActivity;
import Activity.MessageActivity;
import Activity.NoteStudentActivity;
import Activity.PrivateActivity;
import Activity.SubCategoryActivity;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.ClassSubjectResult;
import Models.NewsClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static Toolbar toolbar;
    private ImageView myProfileImage;
    private TextView myPersonName, myUserName;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Explore");
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);


        myProfileImage = hView.findViewById(R.id.my_profile_image);
        myPersonName = hView.findViewById(R.id.my_person_name);
        myUserName = hView.findViewById(R.id.my_user_name);
        myPersonName.setVisibility(View.GONE);
        myUserName.setVisibility(View.GONE);

        MenuItem addHomework = navigationView.getMenu().findItem(R.id.nav_add_homework),
                homework = navigationView.getMenu().findItem(R.id.nav_homework),
                document= navigationView.getMenu().findItem(R.id.nav_document);
        addHomework.setVisible(false);
        homework.setVisible(false);
        document.setVisible(false);
        if (Common.getUser() != null) {
            onCreateProfile();
            if(Common.getUser().getType().userType == 0 || Common.getUser().getType().userType == 1) {
                document.setVisible(true);
            }

            if (Common.getUser().getType().userType == 1) {
                addHomework.setVisible(true);
            } else if (Common.getUser().getType().userType == 3) {
                homework.setVisible(true);
            }
        }

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ExploreActivity()).commit();
            navigationView.setCheckedItem(R.id.nav_explore);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Api api = DataFromApi.getApi();
        switch (item.getItemId()) {
            case R.id.nav_explore:
                toolbar.setTitle("Explore");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ExploreActivity()).commit();
                break;

            case R.id.nav_private:
                if (Common.getUser() != null) {
                    toolbar.setTitle("Private");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PrivateActivity()).commit();
                } else
                    Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_subcategories:
                toolbar.setTitle("Subcategories");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SubCategoryActivity()).commit();
                break;

            case R.id.nav_account:
                if (Common.getUser() == null) {
                    toolbar.setTitle("Login");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AccountActivity()).commit();
                } else {
                    startDialog();
                }
                break;

            case R.id.nav_add_news:
                if (Common.getUser() != null) {
                    toolbar.setTitle("Add News");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AddNewsActivity()).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_message:
                if (Common.getUser() != null) {
                    toolbar.setTitle("Message");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MessageActivity()).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_note:
                if (Common.getUser() != null) {
                    toolbar.setTitle("Students");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new NoteStudentActivity()).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_homework:
                toolbar.setTitle("Homework");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeworkActivity()).commit();
                break;

            case R.id.nav_add_homework:
                toolbar.setTitle("Add homework");

                if(true){
                    Call<ClassSubjectResult> call = api.GetClasses(Common.getUser().id);

                    call.enqueue(new Callback<ClassSubjectResult>() {
                        @Override
                        public void onResponse(Call<ClassSubjectResult> call, Response<ClassSubjectResult> response) {
                            ClassSubjectResult result = response.body();
                            if (result.statusCode == 200 && result.results.size() > 0) {
                                Common.classes = result.results;
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new AddHomeworkActivity()).commit();
                            }
                        }


                        @Override
                        public void onFailure(Call<ClassSubjectResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                break;


            case R.id.nav_document:
                if (Common.getUser() != null) {
                    if (Common.getUser().getType().userType == 1 || Common.getUser().getType().userType == 2) {
                        Intent i = new Intent(NewsActivity.this, DocumentClassesSubjectsActivity.class);
                        startActivity(i);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.nav_exam:
                if (Common.getUser() != null) {
                    if(Common.getUser().getType().userType == 1) {
                        Intent i = new Intent(NewsActivity.this, ExamTeacherClassesActivity.class);
                        startActivity(i);
                    } else if(Common.getUser().getType().userType == 2) {
                        toolbar.setTitle("Your children");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ExamStudentActivity()).commit();
                    }else if(Common.getUser().getType().userType == 3){
                        Intent i = new Intent(this, ExamSubjectsActivity.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void startDialog() {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setTitle("Logout");
        myAlertDialog.setMessage("Do you want Logout?");
        Drawable icon = getResources().getDrawable(R.drawable.user);

        if (Common.getUser().profileImage != null) {
            byte[] decodedString = Base64.decode(Common.getUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            icon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 72, 72, false));
        }

        myAlertDialog.setIcon(icon);

        final AlertDialog stopAlert = myAlertDialog.create();
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                stopAlert.cancel();
            }
        });

        myAlertDialog.setPositiveButton("Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Common.getUser().delete();
                        SugarRecord.deleteAll(NewsClass.class, "PRIVATE_NEWS_TYPE <> 0", new String[]{});
                        Intent i = new Intent(NewsActivity.this, NewsCategoryActivity.class);
                        startActivity(i);
                    }
                });
        myAlertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_account);

        if (Common.getUser() != null)
            item.setTitle("Logout");
        else
            item.setTitle("Login");

        return super.onPrepareOptionsMenu(menu);
    }

    private void onCreateProfile() {
        if (Common.getUser().profileImage != null && !Common.getUser().profileImage.equals("")) {
            byte[] decodedString = Base64.decode(Common.getUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            myProfileImage.setImageBitmap(bitmap);
        }

        myPersonName.setVisibility(View.VISIBLE);
        myUserName.setVisibility(View.VISIBLE);
        myPersonName.setText(Common.getUser().fullName);
        myUserName.setText(Common.getUser().userName);
    }


}
