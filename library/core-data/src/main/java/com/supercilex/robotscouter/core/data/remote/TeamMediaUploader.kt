package com.supercilex.robotscouter.core.data.remote

import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.supercilex.robotscouter.core.RobotScouter
import com.supercilex.robotscouter.core.data.R
import com.supercilex.robotscouter.core.model.Team
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import android.util.Log

internal class TeamMediaUploader private constructor(
        team: Team
) : TeamServiceBase<TeamMediaApi>(team, TeamMediaApi::class.java) {
    override fun execute(): Team? {
        if (!File(team.media ?: return null).exists()) return null

        uploadToImgur()
        return team
    }

    private fun uploadToImgur() {
        Log.d("fooddssssssss", "bar")
        val response: Response<JsonObject> = TeamMediaApi.IMGUR_RETROFIT
                .create(TeamMediaApi::class.java)
                .postToImgur(
                        RobotScouter.getString(R.string.imgur_client_id),
                        team.toString(),
                        RequestBody.create(MediaType.parse("image/*"), File(team.media))
                )
                .execute()

        if (cannotContinue(response)) error(response.toString())

        var link: String =
                checkNotNull(response.body()).get("data").asJsonObject.get("link").asString
        // Oh Imgur, why don't you use https by default? 😢
        link = if (link.startsWith("https://")) link else link.replace("http://", "https://")
        // And what about pngs?
        link = if (link.endsWith(".png")) link else link.replace(getFileExtension(link), ".png")

        team.media = link
    }

    /**
     * @return Return a period plus the file extension
     */
    private fun getFileExtension(url: String): String =
            ".${url.split(".").dropLastWhile(String::isEmpty).last()}"

    companion object {
        @WorkerThread
        fun upload(team: Team) = TeamMediaUploader(team).execute()
    }
}
