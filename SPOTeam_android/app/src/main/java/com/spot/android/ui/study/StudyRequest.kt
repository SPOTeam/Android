import com.google.gson.annotations.SerializedName

data class StudyRequest(
    @SerializedName("themes")
    val themes: List<String>,

    @SerializedName("title")
    val title: String,

    @SerializedName("goal")
    val goal: String,

    @SerializedName("introduction")
    val introduction: String,

    @SerializedName("isOnline")
    val isOnline: Boolean,

    @SerializedName("profileImage")
    val profileImage: String? = null,

    @SerializedName("regions")
    val regions: List<String>? = null,

    @SerializedName("maxPeople")
    val maxPeople: Int,

    @SerializedName("gender")
    val gender: Gender,

    @SerializedName("minAge")
    val minAge: Int,

    @SerializedName("maxAge")
    val maxAge: Int,

    @SerializedName("fee")
    val fee: Int,

    @SerializedName("hasFee")
    val hasFee: Boolean? = null
)

enum class Gender {
    @SerializedName("UNKNOWN")
    UNKNOWN,

    @SerializedName("MALE")
    MALE,

    @SerializedName("FEMALE")
    FEMALE
}
enum class StudyFormMode {
    CREATE,
    EDIT
}

data class ApiResponsed(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: StudyResult?
)

data class StudyResult(
    @SerializedName("themes") val themes: List<String>,
    @SerializedName("title") val title: String,
    @SerializedName("goal") val goal: String,
    @SerializedName("introduction") val introduction: String,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("profileImage") val profileImage: String,
    @SerializedName("regions") val regions: List<String>,
    @SerializedName("maxPeople") val maxPeople: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("minAge") val minAge: Int,
    @SerializedName("maxAge") val maxAge: Int,
    @SerializedName("fee") val fee: Int,
    @SerializedName("hasFee") val hasFee: Boolean
)

// 데이터 클래스 정의
data class UploadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String?,
    val result: ResultData
)

data class ResultData(
    val imageUrls: List<ImageUrlData>,
    val imageCount: Int
)

data class ImageUrlData(
    val imageUrl: String,
    val uploadAt: String
)
