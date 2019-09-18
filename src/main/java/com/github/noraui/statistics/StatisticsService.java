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
import org.slf4j.LoggerFactory;

import com.github.noraui.service.impl.HttpServiceImpl;
import com.github.noraui.utils.Messages;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class StatisticsService extends HttpServiceImpl {
    
    
    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);
    
    
    /**
     * @param metrics
     *            metrics object contains all information for user statistics.
     */
    public void share(Statistics metrics) {
        try {
            getClient()
                    .newCall(new Request.Builder().url(STATISTICS_API_GATEWAY + "/" + STATISTICS_VERSION + "/" + STATISTICS_URI)
                            .post(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(metrics))).build())
                    .execute().close();
        } catch (IOException e) {
            LOGGER.error(Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CALL_METICS_API_REST));
        }
    }

}
