package Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import Models.AddMessage;
import Models.CategoryResult;
import Models.ClassSubjectResult;
import Models.DocumentClass;
import Models.DocumentResult;
import Models.EventResult;
import Models.ExamStudentsDontMarkResult;
import Models.ExamSubjectResult;
import Models.ExamTypesResult;
import Models.ExamsStudentResult;
import Models.MessageResult;
import Models.MessageSentResult;
import Models.NewsClass;
import Models.NewsDetailResult;
import Models.NewsResult;
import Models.NoteResult;
import Models.NoteStudentResult;
import Models.Result;
import Models.UserResult;
import Models.UsersResult;
import Models.VotingResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    //    String HOST = "http://212.11.218.161:85/api/";
    String HOST = "http://212.11.196.194/api/";
//    String HOST = "http://192.168.1.135/api/";
    int schoolId = 1104;

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();


    @GET("news/get?school_id=" + schoolId)
    Call<NewsResult> News(@Query("sub_cat_id") long subCatId, @Query("page") int page);

    @GET("news/detail")
    Call<NewsDetailResult> NewsDetail(@Query("news_id") long newsId);


    @GET("event/get?school_id=" + schoolId)
    Call<EventResult> Events();

    @GET("voting/get?school_id=" + schoolId)
    Call<VotingResult> Voting(@Query("user_id") int userId, @Query("page") int page);

    @GET("voting/vote")
    Call<Result> AddVote(@Query("user_id") long userId, @Query("choice_id") long choiceId);

    @GET("voting/del")
    Call<Result> DelVote(@Query("user_id") long userId, @Query("choice_id") long choiceId);

    @GET("news/cats?school_id=" + schoolId)
    Call<CategoryResult> NewsCategory();

    @GET("news/group?school_id=" + schoolId)
    Call<NewsResult> GroupNews(@Query("sub_cat_id") long subCatId, @Query("user_id") long userId);

    @GET("news/class?school_id=" + schoolId)
    Call<NewsResult> ClassNews(@Query("sub_cat_id") long subCatId, @Query("user_id") long userId);

    @GET("news/subject?school_id=" + schoolId)
    Call<NewsResult> SubjectNews(@Query("sub_cat_id") long subCatId, @Query("user_id") long userId);

    @POST("news/add?school_id=" + schoolId)
    Call<NewsResult> AddNews(@Body NewsClass news);

    @GET("user/signin")
    Call<UserResult> SignIn(@Query("user_name") String userName, @Query("password") String password);

    @GET("user/get?school_id=" + schoolId)
    Call<UsersResult> GetAllUser();

    @GET("user/update")
    Call<UserResult> Update(@Query("user_id") long userId, @Query("full_name") String fullName);

    @GET("homework/getclasses")
    Call<ClassSubjectResult> GetClasses(@Query("user_id") long userId);

    @GET("user/getsubjects")
    Call<ClassSubjectResult> GetSubjects(@Query("user_id") long userId);

    @POST("user/update")
    Call<UserResult> Update(@Query("user_id") long userId, @Query("full_name") String fullName, @Body String image);

    @GET("news/profile?school_id=" + schoolId)
    Call<NewsResult> ProfileNews(@Query("user_id") long userId);

    @GET("message/get")
    Call<MessageResult> Message(@Query("user_id") long userId, @Query("page") int page);

    @GET("message/getsent")
    Call<MessageSentResult> MessageSent(@Query("user_id") long userId, @Query("page") int page);

    @GET("message/isread")
    Call<Result> MessageIsRead(@Query("user_id") long userId, @Query("message_id") long messageId);

    @POST("message/add?school_id=" + schoolId)
    Call<MessageResult> AddMessage(@Query("user_id") long userId, @Body AddMessage message);

    @GET("homework/add?school_id=" + schoolId)
    Call<MessageResult> AddHomework(@Query("user_id") long userId, @Query("subject_id") long subjectId, @Query("title") String title, @Query("body") String body);

    @GET("homework/get")
    Call<MessageResult> Homework(@Query("user_id") long userId, @Query("page") int page);

    @GET("message/gethomework")
    Call<Result> GetHomework(@Query("user_id") long userId);

    @GET("note/getstudents")
    Call<NoteStudentResult> NoteStudent(@Query("user_id") long userId);

    @GET("note/get")
    Call<NoteResult> Note(@Query("student_id") long studentId);

    @GET("note/add")
    Call<Result> AddNote(@Query("user_id") long userId, @Query("student_id") long studentId, @Query("note") String note);

    @GET("document/get")
    Call<DocumentResult> GetDocuments(@Query("user_id") long userId, @Query("subject_id") long subjectId);


    @POST("document/add")
    Call<Result> AddDocument(@Query("user_id") long userId, @Query("subject_id") long subjectId, @Query("student_id") long studentId, @Body DocumentClass document);

    @GET("exam/children")
    Call<NoteStudentResult> GetChildren(@Query("user_id") long userId);

    @GET("exam/getsubjects")
    Call<ExamSubjectResult> GetMarks(@Query("user_id") long userId);

    @GET("exam/getexams")
    Call<ExamsStudentResult> GetExamsStudents(@Query("subject_id") long subjectId);

    @GET("exam/getexamtype")
    Call<ExamTypesResult> GetExamTypes(@Query("subject_id") long subjectId);

    @GET("exam/getstudents")
    Call<ExamStudentsDontMarkResult> GetStudentsDontMark(@Query("exam_id") long examId);

    @GET("exam/addmark")
    Call<Result> AddMark(@Query("student_id") long studentId, @Query("exam_id") long examId, @Query("absent") int absent, @Query("mark") double mark);

    @GET("exam/addexam")
    Call<Result> AddExam(@Query("user_id") long userId, @Query("subject_id") long subjectId, @Query("exam_type_id") long examTypeId, @Query("max") int max,
                         @Query("min") int min, @Query("exam_name") String name, @Query("date") Date date);
}
