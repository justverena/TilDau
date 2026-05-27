package com.example.tildau.data.model.stats

import com.google.gson.annotations.SerializedName

data class SkillTrendDto(
    @SerializedName("overall")
    val overall: Double,

    @SerializedName("fluency")
    val fluency: Double,

    @SerializedName("pronunciation")
    val pronunciation: Double
)