package com.example.bimbel.network;
import com.example.bimbel.model.Guru;
import com.example.bimbel.model.ProfileResponse;
import com.example.bimbel.response.AccessToken;
import com.example.bimbel.response.Chat.ChatGuru;
import com.example.bimbel.response.Chat.ChatSiswa;
import com.example.bimbel.response.Chat.DataChat;
import com.example.bimbel.response.Guru.ProfileGuru;
import com.example.bimbel.response.Guru.ProfileGuruResponse;
import com.example.bimbel.response.History.HistoryGuru;
import com.example.bimbel.response.History.HistorySiswa;
import com.example.bimbel.response.Kelas.DataKelas;
import com.example.bimbel.response.MataPelajaran.DataMataPelajaran;
import com.example.bimbel.response.MateriGuru.MateriGuru;
import com.example.bimbel.response.Siswa.Akun;
import com.example.bimbel.response.Siswa.AkunResponse;
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.Guru.GuruResponse;
import com.example.bimbel.response.Siswa.Siswa;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    @FormUrlEncoded
    @POST("register")
    Call<AccessToken> register(
      @Field("username") String username,
      @Field("email") String email,
      @Field("password") String password,
      @Field("nama") String nama,
      @Field("level") String level
    );

    @FormUrlEncoded
    @POST("login")
    Call<AccessToken> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login/data")
    Call<Akun> logindata(@Field("username") String username);

    @GET("setting")
    Call<AkunResponse> setting();

    @FormUrlEncoded
    @PATCH("ubahakun")
    Call<ResponseBody> ubahakun(@Field("username") String username, @Field("email") String email, @Field("password") String password);

    @GET("siswa")
    Call<List<Siswa>> siswa();

    @GET("siswa/profile")
    Call<ProfileResponse> profile_siswa();

    @GET("guru")
    Call<List<Guru>> guru();

    @Multipart
    @POST("siswa")
    Call<ResponseBody> ubahProfile(
        @Part("tanggal_lahir_siswa") RequestBody tanggal_lahir_siswa,
        @Part("jenkel_siswa") RequestBody jenkel_siswa,
        @Part("agama_siswa") RequestBody agama_siswa,
        @Part("alamat_siswa") RequestBody alamat_siswa,
        @Part("no_handphone_siswa") RequestBody no_handphone_siswa,
        @Part MultipartBody.Part foto_siswa,
        @Part("nama") RequestBody nama
    );

    @GET("berita")
    Call<List<Berita>> berita();

    @GET("dataguru")
    Call<GuruResponse> dataguru();

    @GET("berita/{id}")
    Call<Berita> detailberita(@Path("id") String id);

    @GET("kelas")
    Call<DataKelas> kelas();

    @FormUrlEncoded
    @POST("mata-pelajaran")
    Call<DataMataPelajaran> pelajaran(@Field("kelas") String kelas);

    @FormUrlEncoded
    @POST("guru/carigurukelas")
    Call<List<Guru>> carigurukelas(@Field("nama") String nama, @Field("kelas") String kelas, @Field("pelajaran") String pelajaran);

    @GET("guru/show")
    Call<ProfileGuru> profileguru();

    @Multipart
    @POST("guru")
    Call<ResponseBody> ubahProfileguru(
            @Part("tanggal_lahir_guru") RequestBody tanggal_lahir_guru,
            @Part("jenkel_guru") RequestBody jenkel_guru,
            @Part("agama_guru") RequestBody agama_guru,
            @Part("alamat_guru") RequestBody alamat_guru,
            @Part("no_handphone_guru") RequestBody no_handphone_guru,
            @Part MultipartBody.Part foto_guru,
            @Part("id_category_kelas") RequestBody id_category_kelas,
            @Part("id_mata_pelajaran") RequestBody id_mata_pelajaran,
            @Part("nama") RequestBody nama
    );

    @GET("guru/detailguru/{id}")
    Call<List<Guru>> detailguru(@Path("id") String id);

    @FormUrlEncoded
    @POST("history")
    Call<ResponseBody> simpanHistory(@Field("kode_guru") String kode_guru);

    @GET("historyguru")
    Call<List<HistoryGuru>> historyguru();

    @GET("historysiswa")
    Call<List<HistorySiswa>> historysiswa();

    @FormUrlEncoded
    @POST("detailhistorysiswa")
    Call<List<HistorySiswa>> detailhistorysiswa(@Field("kode_guru") String kode_guru, @Field("id_history") String id_history);

    @FormUrlEncoded
    @POST("detailhistoryguru")
    Call<List<HistoryGuru>> detailhistoryguru(@Field("kode_siswa") String kode_siswa, @Field("id_history") String id_history);

    @DELETE("history/{id}")
    Call<ResponseBody> batalkanBooking(@Path("id") String id);

    @FormUrlEncoded
    @PATCH("history/terima")
    Call<ResponseBody> terimaBooking(@Field("id_history") String id_history);

    @FormUrlEncoded
    @PATCH("history/tolak")
    Call<ResponseBody> tolakBooking(@Field("id_history") String id_history);

    @FormUrlEncoded
    @PATCH("history/selesai")
    Call<ResponseBody> selesaiBooking(@Field("id_history") String id_history, @Field("kode_guru") String kode_guru);

    @GET("materi-guru/tampil")
    Call<List<HistoryGuru>> materiguru();

    @Multipart
    @POST("materi-guru")
    Call<ResponseBody> uploadmateri(
            @Part("nama_materi") RequestBody nama_materi,
            @Part MultipartBody.Part file_materi,
            @Part("kode_siswa") RequestBody kode_siswa
    );

    @GET("materi-guru")
    Call<List<MateriGuru>> materisiswa();

    @GET
    Call<ResponseBody> download(@Url String fileUrl);

    @GET("chat/siswa")
    Call<List<ChatSiswa>> chatsiswa();

    @GET("chat/guru")
    Call<List<ChatGuru>> chatguru();

    @GET("chat/guru/{id}")
    Call<List<Siswa>> guruchat(@Path("id") String id);

    @GET("chat/siswa/{id}")
    Call<List<ProfileGuruResponse>> siswachat(@Path("id") String id);

    @FormUrlEncoded
    @POST("chat")
    Call<ResponseBody> chat(@Field("kode_guru") String kode_guru, @Field("isi_chat") String isi_chat, @Field("kode_siswa") String kode_siswa);

    @GET("detail/chat/guru/{id}")
    Call<List<DataChat>> coba(@Path("id") String id);

}