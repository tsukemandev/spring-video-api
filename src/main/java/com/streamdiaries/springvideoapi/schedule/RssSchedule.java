package com.streamdiaries.springvideoapi.schedule;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import com.apptasticsoftware.rssreader.RssReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamdiaries.springvideoapi.entity.Rss;
import com.streamdiaries.springvideoapi.repository.RssRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RssSchedule {

    @Autowired
    private RssRepository rssRepository;

    @Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
    public void scheduledTask() throws JsonProcessingException, IOException {
        System.out.println("스케줄러가 1분마다 실행됩니다. " + System.currentTimeMillis());
        ObjectMapper objectMapper = new ObjectMapper();
        rssRepository.save(Rss.builder().code("movie").content(objectMapper.writeValueAsString(getRssFeedItems())).build());

        System.out.println("rss row count :  " + rssRepository.findAllByCode("movie").size());
    }


    public List<RssFeedItem> getRssFeedItems() throws IOException {
        List<RssFeedItem> list = new ArrayList<>();
        RssReader rssReader = new RssReader();

        log.info("===================================================");
        rssReader.read("https://screenrant.com/feed/movie-news/").collect(Collectors.toList()).forEach(el -> {

            String pubDate = "";
            LocalDateTime givenDateTime = getPubLocalDate(el.getPubDate().orElse(""));

            if (givenDateTime != null) {
                // 현재의 GMT 기준 시각을 가져오기
                Instant currentGMT = Instant.now();

                // GMT 기준 시각을 사용하여 LocalDateTime 객체로 변환
                LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentGMT, ZoneId.of("GMT"));

                // 주어진 LocalDateTime 객체와 현재 시간 사이의 차이 (분 단위)
                long minutesAgo = ChronoUnit.MINUTES.between(givenDateTime, currentDateTime);

                if (minutesAgo < 60) {
                    pubDate = minutesAgo + "분전";
                } else if (minutesAgo / 60 < 24) {
                    pubDate = (minutesAgo / 60) + "시간전";
                } else if (minutesAgo / 60 >= 24) {
                    pubDate = (minutesAgo / 60) / 24 + "일전";
                }
            }

            list.add(RssFeedItem.builder()
                    .title(el.getTitle().orElse(""))
                    .description(el.getDescription().orElse(""))
                    .author(el.getAuthor().orElse(""))
                    .link(el.getLink().orElse(""))
                    .parmaLink(el.getIsPermaLink().orElse(false))
                    .pubDate(pubDate)
                    .comments(el.getComments().orElse(""))
                    .category(el.getCategories())
                    .guid(el.getGuid().orElse(""))
                    .channelTitle(el.getChannel().getTitle())
                    .build());

        });

        list.forEach(o -> System.out.println("rss item : " + o.getTitle()));

        return list;

    }

    public LocalDateTime getPubLocalDate(String dateString) {
        if ("".equals(dateString)) {
            return null;
        }
        // DateTimeFormatter 정의 (주어진 날짜 형식에 맞춰야 함)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

        // 문자열을 ZonedDateTime으로 파싱
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);

        // ZonedDateTime을 LocalDateTime으로 변환
        return zonedDateTime.toLocalDateTime();

    }

    @Getter
    @AllArgsConstructor
    @Builder
    static class RssFeedItem {

        private final String title;
        private final String description;
        private final String author;
        private final String link;
        private final Boolean parmaLink;
        private final String pubDate;
        private final String comments;
        private final List<String> category;
        private final String guid;
        private final String channelTitle;

    }
}
