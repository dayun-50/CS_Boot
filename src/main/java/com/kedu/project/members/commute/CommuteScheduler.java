package com.kedu.project.members.commute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kedu.project.holiday.HolidayService;
import com.kedu.project.holiday.HolidayService;
import java.time.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CommuteScheduler {

    @Autowired private CommuteService commuteService;
    @Autowired private HolidayService holidayService;
    
    
    //매일 12시 59분 실행
    @Scheduled(cron = "0 59 23  * * *")
    public void updateAbsenceForMissingCommute() {
        LocalDate today = LocalDate.now();

        // 1. 주말/공휴일 스킵
        if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) return;
        if (holidayService.isHoliday(today)) return;

        // 2. 서비스 호출
        int updated = commuteService.updateAbsenceForToday(today);
        System.out.println("[스케줄러] " + (updated > 0 ? updated + "명 결근 처리 완료" : "결근자 없음"));
    }
}

