package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.model.OosResult;
import com.bftcom.devtournament.checker.util.HtmlParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

@Service
public class OutgoingManager {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final String WAIT_MSG = "The break between submissions must be at least 10 seconds";

  // sumbit
  public SubmitResult sumbit(String judgeId, long langId, long problemNum, String sourceCode) {
    try {
      HttpEntity requestEntity = MultipartEntityBuilder.create()
          .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
          .addTextBody("Action", "submit")
          .addTextBody("SpaceID", "1")
          .addTextBody("JudgeID", judgeId)
          .addTextBody("Language", String.valueOf(langId))
          .addTextBody("ProblemNum", String.valueOf(problemNum))
          .addTextBody("Source", sourceCode)
          .build();
      SubmitResult submitResult = Request.Post("http://acm.timus.ru/submit.aspx?space=1")
          .body(requestEntity).execute().handleResponse(response -> {
            HttpEntity responseEntity = response.getEntity();
            int responseStatusCode = response.getStatusLine().getStatusCode();
            String responseString = EntityUtils.toString(responseEntity);
            log.debug("HTTP Response = \nHTTP code=" + responseStatusCode + "\n" + responseString);
            if (responseStatusCode == HttpURLConnection.HTTP_OK)
              return StringUtils.containsIgnoreCase(responseString, WAIT_MSG) ? SubmitResult.WAIT : SubmitResult.OK;
            else
              return SubmitResult.ERROR;
          });
      return submitResult;
    } catch (IOException e) {
      log.error(null, e);
      return SubmitResult.ERROR;
    }
  }


  public List<OosResult> getOosResultList(String num, String author) {
    try {
      // http://acm.timus.ru/status.aspx?space=1&num=1000&author=230361
      List<OosResult> result = Request.Get("http://acm.timus.ru/status.aspx?space=1&num=" + num + "&author=" + author)
          .execute().handleResponse(response -> {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
              return HtmlParser.parseOosResultList(responseString);
            } else {
              return null;
            }
          });
      return result;
    } catch (IOException e) {
      log.error(null, e);
      return null;
    }
  }
}
