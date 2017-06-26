package com.bftcom.devtournament.checker;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author a.solonshchikov
 *         Date: 26.06.2017
 */
@Component
public class OutgoingManager {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final String WAIT_MSG = "The break between submissions must be at least 10 seconds";

  @Autowired
  HtmlParser htmlParser;

  // sumbit
  public SubmitResult sumbit(String sourceCode) {
    try {
      HttpEntity requestEntity = MultipartEntityBuilder.create()
          .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
          .addTextBody("Action", "submit")
          .addTextBody("SpaceID", "1")
          .addTextBody("JudgeID", "230361FH")
          .addTextBody("Language", "31")
          .addTextBody("ProblemNum", "1000")
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
              return htmlParser.parseOosResultList(responseString);
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
