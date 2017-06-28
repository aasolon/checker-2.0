package com.bftcom.devtournament.checker.util;

import com.bftcom.devtournament.checker.model.OosResult;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser {
  public static List<OosResult> parseOosResultList(String html) {
    List<OosResult> oosResultList = new ArrayList<>();

    Document doc = Jsoup.parse(html);
    Elements oosResultListEl = doc.select("tr.even,tr.odd");

    for (Element oosResultEl : oosResultListEl) {
      String id = oosResultEl.select("td[class=id]").first().text();
      String verdict = oosResultEl.select("td[class^=verdict]").first().text();
      String testNumber = oosResultEl.select("td[class=test]").first().text();
      String runtime = oosResultEl.select("td[class=runtime]").first().text();
      String memory = oosResultEl.select("td[class=memory]").first().text();
      OosResult oosResult = new OosResult(
          Long.parseLong(id),
          verdict,
          StringUtils.isNotEmpty(testNumber) ? Integer.parseInt(testNumber) : null,
          StringUtils.isNotEmpty(runtime) ? new BigDecimal(runtime) : null,
          StringUtils.defaultIfEmpty(memory, null)
      );
      oosResultList.add(oosResult);
    }

    return oosResultList;
  }
}
