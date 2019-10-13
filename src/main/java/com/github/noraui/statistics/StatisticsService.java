/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.statistics;

import static com.github.noraui.statistics.StatisticsConfig.STATISTICS_API_GATEWAY;
import static com.github.noraui.statistics.StatisticsConfig.STATISTICS_URI;
import static com.github.noraui.statistics.StatisticsConfig.STATISTICS_VERSION;

import java.io.IOException;

import org.slf4j.Logger;

import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.service.impl.HttpServiceImpl;
import com.github.noraui.utils.Messages;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Loggable
public class StatisticsService extends HttpServiceImpl {

    static Logger log;

    private Callback responseCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            log.error(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CALL_METICS_API_REST));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            log.debug(Messages.getMessage(Messages.THANK_YOU_FOR_YOUR_CONTRIBUTION));
        }
    };

    /**
     * If you use this method, you have agreed to share your usage statistics and configuration of your robot with the NoraUi team anonymously.
     * This was asked when generating the robot via the artifact Maven. This allows us to improve the user experience in future versions.
     * This information is not used for commercial purposes.
     * 
     * @param statistics
     *            statistics object contains all informations for user statistics.
     * @param uuid
     *            is a universally unique identifier.
     */
    public void share(Statistics statistics, String uuid) {
        getClient().newCall(new Request.Builder().url(STATISTICS_API_GATEWAY + "/" + STATISTICS_VERSION + "/" + STATISTICS_URI + "/" + uuid)
                .post(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(statistics))).build()).enqueue(responseCallback);
    }

}
