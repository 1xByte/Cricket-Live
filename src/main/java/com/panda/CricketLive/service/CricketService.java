package com.panda.CricketLive.service;

import com.panda.CricketLive.model.MatchEntity;
import com.panda.CricketLive.repo.CricketRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class CricketService {


    private CricketRepo matchRepo;

    @Autowired
    public CricketService(CricketRepo matchRepo) {
        this.matchRepo = matchRepo;
    }

    public List<MatchEntity> getLiveMatchScores() {
        List<MatchEntity> matchEntities = new ArrayList<>();
        try {
            String url = "https://www.cricbuzz.com/cricket-match/live-scores";
            Document document = Jsoup.connect(url).get();
            Elements liveScoreElements = document.select("div.cb-mtch-lst.cb-tms-itm");
            for (Element match : liveScoreElements) {
                HashMap<String, String> liveMatchInfo = new LinkedHashMap<>();
                String teamsHeading = match.select("h3.cb-lv-scr-mtch-hdr").select("a").text();
                String matchNumberVenue = match.select("span").text();
                Elements matchBatTeamInfo = match.select("div.cb-hmscg-bat-txt");
                String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
                Elements bowlTeamInfo = match.select("div.cb-hmscg-bwl-txt");
                String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
                String textLive = match.select("div.cb-text-live").text();
                String textComplete = match.select("div.cb-text-complete").text();
                //getting match link
                String matchLink = match.select("a.cb-lv-scrs-well.cb-lv-scrs-well-live").attr("href").toString();

                MatchEntity matchEntity1 = new MatchEntity();
                matchEntity1.setTeamHeading(teamsHeading);
                matchEntity1.setMatchNumberVenue(matchNumberVenue);
                matchEntity1.setBattingTeam(battingTeam);
                matchEntity1.setBattingTeamScore(score);
                matchEntity1.setBowlTeam(bowlTeam);
                matchEntity1.setBowlTeamScore(bowlTeamScore);
                matchEntity1.setLiveText(textLive);
                matchEntity1.setMatchLink(matchLink);
                matchEntity1.setTextComplete(textComplete);
                matchEntity1.setMatchStatus();


                matchEntities.add(matchEntity1);

//                update the match in database


               updateMatch(matchEntity1);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matchEntities;
    }

    private void updateMatch(MatchEntity matchEntity1) {

        MatchEntity matchEntity = this.matchRepo.findByTeamHeading(matchEntity1.getTeamHeading());
        if (matchEntity == null) {
            this.matchRepo.save(matchEntity1);
        } else {

            matchEntity1.setMatchId(matchEntity.getMatchId());
            this.matchRepo.save(matchEntity1);
        }

    }

    public List<List<String>> getCWC2023PointTable() {
        List<List<String>> pointTable = new ArrayList<>();
        String tableURL = "https://www.cricbuzz.com/cricket-series/6732/icc-cricket-world-cup-2023/points-table";
        try {
            Document document = Jsoup.connect(tableURL).get();
            Elements table = document.select("table.cb-srs-pnts");
            Elements tableHeads = table.select("thead>tr>*");
            List<String> headers = new ArrayList<>();
            tableHeads.forEach(element -> {
                headers.add(element.text());
            });
            pointTable.add(headers);
            Elements bodyTrs = table.select("tbody>*");
            bodyTrs.forEach(tr -> {
                List<String> points = new ArrayList<>();
                if (tr.hasAttr("class")) {
                    Elements tds = tr.select("td");
                    String team = tds.get(0).select("div.cb-col-84").text();
                    points.add(team);
                    tds.forEach(td -> {
                        if (!td.hasClass("cb-srs-pnts-name")) {
                            points.add(td.text());
                        }
                    });
//                    System.out.println(points);
                    pointTable.add(points);
                }


            });

            System.out.println(pointTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pointTable;
    }

    public List<MatchEntity> getAllMatches() {
        return this.matchRepo.findAll();
    }
}
