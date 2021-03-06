package com.bftcom.devtournament.checker.service;

import com.bftcom.devtournament.checker.exception.UserException;
import com.bftcom.devtournament.checker.model.OosResult;
import com.bftcom.devtournament.checker.util.HtmlParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
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
  private static final String PROPERTY_ORIGINAL = "ONLINE_JUDGE";
  private static final String PROPERTY_BFT = "BFT_CHECKER";

  public enum SubmitResult {
    OK(null),
    WAIT("Интервал между попытками должен составлять не менее 10 сек.");

    private String msg;

    SubmitResult(String msg) {
      this.msg = msg;
    }

    public String getMsg() {
      return msg;
    }
  }

  // sumbit
  public SubmitResult sumbit(String judgeId, long langId, long problemNum, String sourceCode) throws IOException {
    sourceCode = replaceProperty(sourceCode);
    HttpEntity requestEntity = MultipartEntityBuilder.create()
        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
        .addTextBody("Action", "submit")
        .addTextBody("SpaceID", "1")
        .addTextBody("JudgeID", judgeId)
        .addTextBody("Language", String.valueOf(langId))
        .addTextBody("ProblemNum", String.valueOf(problemNum))
        .addTextBody("Source", sourceCode)
        .build();
    log.info("HTTP POST Request = \nURL=http://acm.timus.ru/submit.aspx?space=1\n" +
        "Params:\nAction=submit\nSpaceID=1\nJudgeID=" + judgeId + "\nLanguage=" + String.valueOf(langId) + "\n" +
        "ProblemNum=" + String.valueOf(problemNum) + "\nSource=" + sourceCode);
    SubmitResult submitResult = Request.Post("http://acm.timus.ru/submit.aspx?space=1")
        .body(requestEntity).execute().handleResponse(response -> {
          HttpEntity responseEntity = response.getEntity();
          int responseStatusCode = response.getStatusLine().getStatusCode();
          if (responseStatusCode != HttpURLConnection.HTTP_OK)
            throw new UserException("Непредвиденная ошибка при отправке решения, попробуйте повторить попытку");
          String responseString = EntityUtils.toString(responseEntity);
          log.info("HTTP Response = \nHTTP code=" + responseStatusCode + "\n" + responseString);
          return StringUtils.containsIgnoreCase(responseString, WAIT_MSG) ? SubmitResult.WAIT : SubmitResult.OK;
        });
    return submitResult;
  }


  public List<OosResult> getOosResultList(long num, long author) throws IOException {
    log.info("HTTP GET Request = \nURL=http://acm.timus.ru/status.aspx?space=1&num=" + num + "&author=" + author);
    List<OosResult> oosResultList = Request.Get("http://acm.timus.ru/status.aspx?space=1&num=" + num + "&author=" + author)
        .execute().handleResponse(response -> {
          HttpEntity responseEntity = response.getEntity();
          int responseStatusCode = response.getStatusLine().getStatusCode();
          if (responseStatusCode != HttpURLConnection.HTTP_OK)
            throw new UserException("Непредвиденная ошибка при отправке решения, попробуйте повторить попытку");
          String responseString = EntityUtils.toString(responseEntity);
          log.info("HTTP Response = \nHTTP code=" + responseStatusCode + "\n" + responseString);
          return HtmlParser.parseOosResultList(responseString);
        });
    return oosResultList;
  }

  public String getCompilationError(String judgeId, long resultOosKey) throws IOException {
    List<NameValuePair> postParams = Form.form()
        .add("Action", "login")
        .add("Source", "/ce.aspx?id=" + resultOosKey)
        .add("JudgeID", judgeId)
        .build();
    log.info("HTTP POST Request =\nURL=http://acm.timus.ru/auth.aspx\nParams:\nAction=login\nSource=/ce.aspx?id=" + resultOosKey +
        "\nJudgeID=judgeId");
    String compilationError = Request.Post("http://acm.timus.ru/auth.aspx")
        .bodyForm(postParams)
        .execute().handleResponse(response -> {
          HttpEntity responseEntity = response.getEntity();
          int responseStatusCode = response.getStatusLine().getStatusCode();
          if (responseStatusCode != HttpURLConnection.HTTP_OK)
            throw new UserException("Непредвиденная ошибка при отправке решения, попробуйте повторить попытку");
          String responseString = EntityUtils.toString(responseEntity);
          log.info("HTTP Response = \nHTTP code=" + responseStatusCode + "\n" + responseString);
          return responseString;
        });
    return compilationError;
  }

  private static String replaceProperty(String sourceCode) {
    return sourceCode
        .replaceAll(PROPERTY_ORIGINAL, PROPERTY_ORIGINAL.replaceAll(".", "x"))
        .replaceAll(PROPERTY_BFT, PROPERTY_ORIGINAL);
  }
}
