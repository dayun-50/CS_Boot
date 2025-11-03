package com.kedu.project.holiday;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class HolidayService {
    private static final String API_URL = "https://date.nager.at/api/v3/PublicHolidays/{year}/KR"; //네이버 공휴일 api
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isHoliday(LocalDate date) {
        try {
        	// 1. API 호출 → 해당 연도의 모든 공휴일 JSON 받아오기
            HolidayDTO[] response = restTemplate.getForObject(API_URL, HolidayDTO[].class, date.getYear());
            // 2. 배열 → List 변환
            List<HolidayDTO> holidays = Arrays.asList(response);
            // 3. 리스트 안에 오늘 날짜가 포함되어 있으면 공휴일로 판단
            return holidays.stream().anyMatch(h -> LocalDate.parse(h.date).equals(date));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
}