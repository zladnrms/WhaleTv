package zladnrms.defytech.kim.BroadcastTv.networking;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkListRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.EndDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.FCMTokenRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.GetBookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.LoginDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ResultRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.RoomDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.StartDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.VideoDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ViewerCountRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ViewerDataRepo;

public interface ApiInterface {

    String folderName = "/whaleTv";

    @FormUrlEncoded
    @POST(folderName + "/member/login.php")
    Observable<LoginDataRepo> loginData(@Field("id") String id, @Field("password") String password);

    @FormUrlEncoded
    @POST(folderName + "/member/autologin.php")
    Observable<LoginDataRepo> autoLoginData(@Field("id") String id, @Field("password") String password);

    @FormUrlEncoded
    @POST(folderName + "/member/login_api.php")
    Observable<LoginDataRepo> apiLoginData(@Field("api") String api, @Field("api_id") String api_id, @Field("api_email") String api_email);

    @FormUrlEncoded
    @POST(folderName + "/member/join.php")
    Observable<ResultRepo> joinData(@Field("id") String id, @Field("password") String password, @Field("nickname") String nickname);

    @FormUrlEncoded
    @POST(folderName + "/room/add_room.php")
    Observable<StartDataRepo> startData( @Field("nickname") String nickname, @Field("id") String id, @Field("subject") String subject);

    @FormUrlEncoded
    @POST(folderName + "/room/del_room.php")
    Observable<EndDataRepo> endData(@Field("roomId") int roomId, @Field("id") String id, @Field("nickname") String nickname, @Field("castTime") int castTime);

    @FormUrlEncoded
    @POST(folderName + "/room/update_room.php")
    Observable<ResultRepo> updateData(@Field("roomId") int roomId);

    @FormUrlEncoded
    @POST(folderName + "/room/change_subject.php")
    Observable<ResultRepo> changeSubject(@Field("roomId") int roomId, @Field("subject") String subject);

    @FormUrlEncoded
    @POST(folderName + "/content/get_bookmark_list.php")
    Observable<BookmarkListRepo> getBookmarkList(@Field("nickname") String nickname);

    @POST(folderName + "/room/get_streaming_room_list.php")
    Observable<RoomDataRepo> roomData();

    @POST(folderName + "/content/get_record_video.php")
    Observable<VideoDataRepo> videoData();

    @FormUrlEncoded
    @POST(folderName + "/content/adj_record_status.php")
    Observable<ResultRepo> changeStatus(@Field("videoId") int videoId, @Field("status") int status);

    @FormUrlEncoded
    @POST(folderName + "/content/adj_record_subject.php")
    Observable<ResultRepo> adjustVideo(@Field("videoId") int videoId, @Field("subject") String subject);

    @FormUrlEncoded
    @POST(folderName + "/content/del_record_video.php")
    Observable<ResultRepo> deleteVideo(@Field("videoId") int videoId, @Field("filename") String filename);

    @FormUrlEncoded
    @POST(folderName + "/content/get_record_video.php")
    Observable<VideoDataRepo> videoData(@Field("nickname") String nickname);

    @FormUrlEncoded
    @POST(folderName + "/content/up_video_count.php")
    Observable<ResultRepo> upVideoCount(@Field("videoId") int videoId);

    @FormUrlEncoded
    @POST(folderName + "/room/get_viewer_in_room.php")
    Observable<ViewerDataRepo> viewerData(@Field("roomId") int roomId);

    @FormUrlEncoded
    @POST(folderName + "/room/get_viewer_count.php")
    Observable<ViewerCountRepo> getViewerCount(@Field("roomId") int roomId);

    @FormUrlEncoded
    @POST(folderName + "/content/add_bookmark.php")
    Observable<BookmarkRepo> addBookmark(@Field("nickname") String nickname, @Field("streamer") String streamer);

    @FormUrlEncoded
    @POST(folderName + "/content/del_bookmark.php")
    Observable<BookmarkRepo> delBookmark(@Field("nickname") String nickname, @Field("streamer") String streamer);

    @FormUrlEncoded
    @POST(folderName + "/content/get_bookmark.php")
    Observable<GetBookmarkRepo> getBookmark(@Field("nickname") String nickname);

    /* Push */
    @FormUrlEncoded
    @POST(folderName + "/fcm/save_token.php")
    Observable<FCMTokenRepo> saveToken(@Field("nickname") String nickname, @Field("token") String token);

    @FormUrlEncoded
    @POST(folderName + "/fcm/bookmark_push.php")
    Observable<ResultRepo> pushBookmark(@Field("nickname") String nickname, @Field("message") String message);
}