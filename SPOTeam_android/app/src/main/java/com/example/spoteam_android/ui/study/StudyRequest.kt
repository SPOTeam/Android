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
    val hasFee: Boolean
)

enum class Gender {
    @SerializedName("UNKNOWN")
    UNKNOWN,

    @SerializedName("MALE")
    MALE,

    @SerializedName("FEMALE")
    FEMALE
}
